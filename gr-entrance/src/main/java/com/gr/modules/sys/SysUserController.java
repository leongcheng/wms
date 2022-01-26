package com.gr.modules.sys;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gr.constant.R;
import com.gr.exception.RRException;
import com.gr.exception.ResultEnum;
import com.gr.exception.ResultException;
import com.gr.utils.IOUtil;
import com.gr.utils.PageUtils;
import com.gr.utils.VerifierUtils;
import com.gr.validator.ValidatorUtils;
import com.gr.validator.group.AddGroup;
import com.gr.validator.group.UpdateGroup;
import com.gr.annotation.RepeatSubmit;
import com.gr.annotation.SysLog;
import com.gr.utils.ShiroUtils;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 用户管理
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("sys/user")
public class SysUserController extends AbstractController
{
	@Autowired
	private SysUserService sysUserService;

	/**
	 * 列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:user:list")
	@ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysUserService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@GetMapping("/info/{userId}")
	@RequiresPermissions("sys:user:info")
	@ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
	public R info(@PathVariable("userId") Long userId){
		SysUserEntity sysUser = sysUserService.getSysUserInfo(userId);

		return R.ok().put("sysUser", sysUser);
	}

	/**
	 * 用户信息
	 */
	@GetMapping("/getUserInfo")
	public R getUserInfo(@RequestParam Map<String, Object> params){
		SysUserEntity loginUser = (SysUserEntity) ShiroUtils.getSubject().getPrincipal();

		return R.ok().put("loginUser", loginUser.setPassword(null));
	}

	/**
	 * 保存
	 */
	@SysLog("新增用户")
	@PostMapping("/save")
	@RequiresPermissions("sys:user:save")
	@ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
	@RepeatSubmit
	public R save(@RequestBody SysUserEntity sysUser){
		ValidatorUtils.validateEntity(sysUser, AddGroup.class);

		boolean result = sysUserService.saveUser(sysUser);
		if (result) {
			return R.ok();
		}
		return R.error();
	}


	/**
	 * 修改
	 */
	@SysLog("修改用户")
	@PostMapping("/update")
	@RequiresPermissions("sys:user:update")
	@ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
	@RepeatSubmit
	public R update(@RequestBody SysUserEntity sysUser){
		ValidatorUtils.validateEntity(sysUser, UpdateGroup.class);

		if (StringUtils.isNotBlank(sysUser.getPhone()) && !VerifierUtils.isChinaPhoneLegal(sysUser.getPhone())) {
			throw new ResultException(ResultEnum.ERR014);
		}
		boolean result = sysUserService.updateUser(sysUser);
		if (result) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 删除
	 */
	@SysLog("删除用户")
	@PostMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	@ApiOperation(value = "删除", notes = "@RequestBody接收ids")
	@RepeatSubmit
	public R delete(@RequestBody Long[] userIds){
		/*List<SysUserEntity> userList = sysUserService.listByIds(Arrays.asList(userIds));

		userList.forEach(sysUserEntity -> {
			sysUserEntity.setDeleted(Constants.SUPER_ADMIN);
		});

		sysUserService.updateBatchById(userList);

		return R.ok();*/

		boolean result = sysUserService.deleteUserIds(userIds);
		if(result){
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 个人中心
	 */
	@SysLog("个人中心")
	@PostMapping("/personal")
	@RequiresPermissions("sys:user:data")
	@ApiOperation(value = "个人中心", notes = "@RequestBody接收实体类")
	@RepeatSubmit
	public R personal(@RequestBody SysUserEntity sysUser){
		if (StringUtils.isNotBlank(sysUser.getPhone()) && !VerifierUtils.isChinaPhoneLegal(sysUser.getPhone())) {
			throw new ResultException(ResultEnum.ERR014);
		}
		sysUserService.updateById(sysUser);

		return R.ok();
	}

	/**
	 * 修改密码
	 */
	@SysLog("修改密码")
	@GetMapping("/updatePassword")
	@RequiresPermissions("sys:user:password")
	@ApiImplicitParam(name = "password", value = "原密码",required = true)
	@ApiOperation(value = "修改密码")
	@RepeatSubmit
	public R updatePassword(String password, String newPassword) {
		if (StringUtils.isBlank(newPassword)) {
			return R.error("新密码不能为空");
		}
		// 原密码
		password = ShiroUtils.sha256(password, getLoginUser().getSalt());

		if(password.equals(getLoginUser().getPassword())){

			// 新密码
			newPassword = ShiroUtils.sha256(newPassword, getLoginUser().getSalt());

			// 更新密码
			boolean result = sysUserService.updateUserPassword(getLoginUserId(), password, newPassword);
			if (result) {
				return R.ok();
			}
		}
		return R.error("原密码不正确");
	}


	/**
	 * 重置密码
	 */
	@SysLog("重置密码")
	@PostMapping("/resetPassword")
	@RequiresPermissions("sys:user:reset")
	@ApiOperation(value = "重置密码", notes = "@RequestBody接收ids")
	public R resetPassword(@RequestBody Long[] ids) {
		List<SysUserEntity> user = sysUserService.listByIds(Arrays.asList(ids));
		user.stream().forEach(item ->{
			//初始密码为123456
			String newPassword = ShiroUtils.sha256("123456", item.getSalt());
			//更新密码
			sysUserService.updateUserPassword(item.getUserId(), item.getPassword(), newPassword);
		});

		return R.ok();
	}

	/**
	 * 导入
	 */
	@SysLog("用户导入")
	@GetMapping("/import")
	@RequiresPermissions("sys:user:import")
	@ApiOperation(value = "导入", notes = "RequestParam接收Map参数")
	@RepeatSubmit
	public R upload(@RequestParam("file") MultipartFile file){
		try {
			IOUtil.fileTypeVerify(file);

			ImportParams params = new ImportParams();
			//params.setTitleRows(1);
			params.setHeadRows(1);

			List<SysUserEntity> list = ExcelImportUtil.importExcel(file.getInputStream(), SysUserEntity.class, params);

			//过滤为空的
			List<SysUserEntity> filterList = list.stream().filter(map -> StringUtils.isNotBlank(map.getUsername())).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(filterList)){
				filterList.forEach(item -> {
					SysUserEntity sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername, item.getUsername()));
					if(sysUser == null) {
						this.save(item);
					}
				});

				return R.ok();
			}

		} catch (Exception e) {
			throw new RRException(e.getMessage());
		}
		throw new ResultException(ResultEnum.ERR015);
	}


	/**
	 * 导出
	 */
	@PostMapping("/output")
	@RequiresPermissions("sys:user:output")
	@ApiOperation(value = "导出", notes = "RequestParam接收Map参数")
	@RepeatSubmit
	public void output(@RequestParam Map<String, Object> params, HttpServletResponse response, HttpServletRequest request) throws IOException {
		Workbook workbook = null;
		String title = "用户表";
		try{
			ExportParams exportParams = new ExportParams(title, null);
			workbook = ExcelExportUtil.exportBigExcel(exportParams, SysUserEntity.class, new IExcelExportServer() {
				@Override
				public List<Object> selectListForExcelExport(Object o, int page) {
					List<Object> list = new ArrayList<>();
					params.put("page", String.valueOf(page));
					params.put("limit", String.valueOf(o));
					PageUtils pageUtils = sysUserService.queryPage(params);
					pageUtils.getList().forEach(item -> {
						list.add(item);
					});

					return list;
				}
			}, 10000);

			IOUtil.excel(response, request, workbook, title);

		} catch (Exception e) {
			log.error("用户表导出失败{}", e.getMessage());
		}finally {
			if(workbook != null){
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
