package com.gr.modules.sys;

import com.gr.constant.Constants;
import com.gr.constant.R;
import com.gr.utils.PageUtils;
import com.gr.validator.ValidatorUtils;
import com.gr.validator.group.AddGroup;
import com.gr.validator.group.UpdateGroup;
import com.gr.annotation.RepeatSubmit;
import com.gr.annotation.SysLog;
import com.gr.redis.RedisUtils;
import com.gr.sys.entity.SysRoleEntity;
import com.gr.sys.service.SysRoleMenuService;
import com.gr.sys.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 权限角色
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Api(tags = "权限角色")
@RestController
@RequestMapping("sys/role")
public class SysRoleController
{
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private RedisUtils redisUtils;

	/**
	 * 权限列表（分页）
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:role:list")
	@ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = sysRoleService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@GetMapping("/info/{roleId}")
	@RequiresPermissions("sys:role:info")
	@ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
	public R info(@PathVariable("roleId") Long roleId) {
		SysRoleEntity sysRole = sysRoleService.getById(roleId);
		if (sysRole == null) {
			return R.error("权限角色不存在");
		}
		List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdListByRoleId(roleId);
		sysRole.setMenuIdList(menuIdList);

		return R.ok().put("sysRole", sysRole);
	}

	/**
	 * 保存
	 */
	@SysLog("新增角色")
	@PostMapping("/save")
	@RequiresPermissions("sys:role:save")
	@ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
	@RepeatSubmit
	public R save(@RequestBody SysRoleEntity sysRole) {
		ValidatorUtils.validateEntity(sysRole, AddGroup.class);

		boolean result = sysRoleService.saveSysRole(sysRole);
		if (result) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 修改
	 */
	@SysLog("修改角色")
	@PostMapping("/update")
	@RequiresPermissions("sys:role:update")
	@ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
	@RepeatSubmit
	public R update(@RequestBody SysRoleEntity sysRole) {
		ValidatorUtils.validateEntity(sysRole, UpdateGroup.class);

		boolean result = sysRoleService.updateSysRole(sysRole);
		if (result) {
			// 清理权限缓存
			redisUtils.delKeys(Constants.PREFIX_SHIRO_CACHE + "*");

			return R.ok();
		}
		return R.error();
	}

	/**
	 * 删除
	 */
	@SysLog("删除角色")
	@PostMapping("/delete")
	@RequiresPermissions("sys:role:delete")
	@ApiOperation(value = "删除", notes = "@RequestBody接收ids")
	@RepeatSubmit
	public R delete(@RequestBody Long[] roleIds) {
		boolean result = sysRoleService.deleteSysRoleByRoleId(roleIds);
		if (result) {
			return R.ok();
		}
		return R.error();
	}

}
