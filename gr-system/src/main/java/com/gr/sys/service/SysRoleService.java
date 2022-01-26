package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysRoleEntity;

import java.util.Map;

/**
 * 权限角色表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysRoleService extends IService<SysRoleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
	 * 保存权限角色信息
	 *
	 * @param sysRole
	 * @return
	 */
	boolean saveSysRole(SysRoleEntity sysRole);

	/**
	 * 更新权限角色信息
	 *
	 * @param sysRole
	 * @return
	 */
	boolean updateSysRole(SysRoleEntity sysRole);

	/**
	 * 删除权限角色信息（包含角色与菜单关系表、角色与用户关系表数据）
	 *
	 * @param roleId
	 * @return
	 */
	boolean deleteSysRoleByRoleId(Long[] roleIds);
}

