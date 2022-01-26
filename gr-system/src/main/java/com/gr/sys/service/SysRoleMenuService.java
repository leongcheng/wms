package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysRoleMenuEntity;

import java.util.List;
import java.util.Map;

/**
 * 角色菜单表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysRoleMenuService extends IService<SysRoleMenuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
	 * 根据权限角色获取菜单编号列表
	 *
	 * @param roleId
	 * @return
	 */
	List<Long> getRoleMenuIdListByRoleId(Long roleId);

	/**
	 * 保存或更新 权限角色与菜单对照关系
	 *
	 * @param roleId
	 * @param menuIdList
	 * @return
	 */
	boolean saveOrUpdateSysRoleMenu(Long roleId, List<Long> menuIdList);

	/**
	 * 根据权限id删除权限角色与菜单对照信息
	 *
	 * @param roleId
	 * @return
	 */
	boolean deleteSysRoleMenuByRoleId(Long roleId);

	/**
	 * 据菜单id删除权限角色与菜单对照信息
	 *
	 * @param menuId
	 * @return
	 */
	boolean deleteByMenuId(Long menuId);
}

