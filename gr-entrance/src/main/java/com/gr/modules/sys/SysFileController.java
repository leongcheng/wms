package com.gr.modules.sys;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gr.constant.R;
import com.gr.exception.ResultEnum;
import com.gr.exception.ResultException;
import com.gr.fdfs.FastDFSClient;
import com.gr.utils.IOUtil;
import com.gr.utils.PageUtils;
import com.gr.uuid.IdUtils;
import com.gr.annotation.RepeatSubmit;
import com.gr.annotation.SysLog;
import com.gr.sys.entity.SysFileEntity;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


/**
 * 文件上传记录表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Slf4j
@RestController
@RequestMapping("sys/file")
@Api(tags = "文件管理")
public class SysFileController
{
	@Autowired
	private SysFileService sysFileService;
	@Autowired
	private FastDFSClient fastDFSClient;

	/**
	 * 列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:file:list")
	@ApiOperation(value = "列表",notes = "@RequestParam接收Map参数")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = sysFileService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@GetMapping("/info/{id}")
	@RequiresPermissions("sys:file:info")
	@ApiOperation(value = "信息",notes = "@PathVariable接收主键id")
	public R info(@PathVariable("id") Long id) {
		SysFileEntity sysFile = sysFileService.getById(id);

		return R.ok().put("sysFile", sysFile);
	}

	/**
	 * 删除
	 */
	@SysLog("文件删除")
	@PostMapping("/delete")
	@RequiresPermissions("sys:file:delete")
	@ApiOperation(value = "删除", notes = "@RequestBody接收数组ids")
	public R delete(@RequestBody Long[] ids) {
		sysFileService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	/**
	 * 附件删除
	 */
	@SysLog("附件删除")
	@PostMapping("/del")
	@RequiresPermissions("sys:file:delete")
	@ApiOperation(value = "附件删除", notes = "@RequestBody接收实体类")
	public R del(@RequestBody SysFileEntity sysFile){
		if(sysFile != null){
			//删除FastDFS服务器上文件
			fastDFSClient.deleteFile(sysFile.getUrl());
			//删除本地
			sysFileService.removeById(sysFile);
		}

		return R.ok();
	}

	/**
	 * 文件上传
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ApiOperation(value = "上传", notes = "@RequestBody接收实体类")
	public R uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			String fileId = fastDFSClient.uploadFile(file);

			SysUserEntity sysUser = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
			SysFileEntity fileEntity = new SysFileEntity();
			fileEntity.setName(file.getOriginalFilename())
					.setUrl(fileId)
					.setFileId(IdUtils.fastSimpleUUID())
					.setCreater(sysUser.getUsername())
					.setCreateTime(LocalDateTime.now());

			sysFileService.save(fileEntity);

			return R.ok().put("fileList", fileEntity);

		} catch (Exception e) {
			log.error("文件上传失败{}", e.getMessage());
		}
		throw new ResultException(ResultEnum.ERR021);
	}


	/**
	 * 单文件下载
	 */
	@SysLog("下载")
	@GetMapping("/downloadFile/{fileId}")
	@ApiOperation(value = "下载", notes = "@PathVariable接收fileId参数")
	@RepeatSubmit
	public void downloadFile(@PathVariable("fileId") String fileId, HttpServletResponse response) {
		SysFileEntity file = sysFileService.getOne(new LambdaQueryWrapper<SysFileEntity>().eq(SysFileEntity::getFileId, fileId));
		if(file != null)
		{
			byte[] data = fastDFSClient.downloadFile(file.getUrl());
			if (data != null && data.length > 0)
			{
				IOUtil.download(response, data, file.getName());
			}
		}

	}

	/**
	 * 打包下载Zip
	 */
	@SysLog("下载Zip")
	@GetMapping("/download/{fileId}")
	@ApiOperation(value = "下载Zip", notes = "@PathVariable接收fileId参数")
	@RepeatSubmit
	public void download(@PathVariable("fileId") String fileId, HttpServletResponse response, HttpServletRequest request){

		List<SysFileEntity> fileList = sysFileService.list(new LambdaQueryWrapper<SysFileEntity>().eq(SysFileEntity::getFileId, fileId));

		Map<String, byte[]> hashMap = new HashMap<>();
		String fileName = "附件";
		fileList.stream().forEach(item -> {
			hashMap.put(item.getName(), fastDFSClient.downloadFile(item.getUrl()));
		});
		IOUtil.outputZip(response, request, hashMap, fileName);
	}

	/**
	 * 导出Excel
	 */
	@RepeatSubmit
	@PostMapping(value = "/export")
	@ApiOperation(value = "导出", notes = "@RequestParam接收map参数")
	public void export(@RequestParam Map<String, Object> params, HttpServletResponse response, HttpServletRequest request) {
		Workbook workbook = null;
		String sheetName = "订单列表";
		try{
			ExportParams exportParams = new ExportParams(sheetName, sheetName);
			workbook = ExcelExportUtil.exportBigExcel(exportParams,  SysFileEntity.class, new IExcelExportServer() {
				@Override
				public List<Object> selectListForExcelExport(Object o, int page) {
					List<Object> list = new ArrayList<>();
					params.put("page", String.valueOf(page));
					params.put("limit", String.valueOf(o));
					PageUtils pageUtils =  sysFileService.queryPage(params);
					pageUtils.getList().forEach(pro->{
						list.add(pro);
					});

					return list;
				}
			}, 10000);

			IOUtil.excel(response, request, workbook, sheetName);

		} catch (Exception e) {
			log.error("导出失败{}", e.getMessage());
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
