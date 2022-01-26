package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.sys.entity.SysMenuEntity;

import java.util.List;

/**
 * 菜单表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysMenuService extends IService<SysMenuEntity> {

	/**
	 * 根据父节点获取所属子节点信息
	 *
	 * @param parentId
	 * @return
	 */
	List<SysMenuEntity> getMenuListByParentId(Long parentId);

	/**
	 * 获取不包含按钮的菜单列表
	 */
	List<SysMenuEntity> queryNotButtonList();

	/**
	 * 根据父节点获取所属子节点信息（含用户校验）
	 *
	 * @param parentId
	 * @param userId
	 * @return
	 */
	List<SysMenuEntity> getMenuListByParentIdAndUserId(Long parentId, Long userId);

	/**
	 * 根据用户获取菜单列表（用于页面树结构菜单）
	 *
	 * @param userId
	 * @return
	 */
	List<SysMenuEntity> getMenuListByUserId(Long userId);

	/**
	 * 获取所有菜单表信息
	 *
	 * @return
	 */
	List<SysMenuEntity> getAllMenuList();

	/**
	 * 小程序根据权限角色获取菜单信息
	 * @param userId
	 * @return
	 */
	List<SysMenuEntity> getMenuListByUserIdMobile(Long userId);

	/**
	 * 根据角色ID查询菜单
	 * @param roleId
	 * @return
	 */
	List<Long> selectMenuListByRoleId(Long roleId);

	/**
	 * 删除菜单
	 * @param menuId
	 */
	boolean deleteByMenuId(Long menuId);

}

