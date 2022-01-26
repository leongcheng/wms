package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色菜单表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenuEntity> {

	/**
	 * 根据权限角色获取菜单编号列表
	 *
	 * @param roleId
	 * @return
	 */
	List<Long> getRoleMenuIdListByRoleId(Long roleId);

	/**
	 * 根据权限角色删除角色菜单对照信息
	 *
	 * @param roleId
	 */
	void deleteRoleMenuByRoleId(Long roleId);

	/**
	 * 根据批量权限角色删除角色菜单对照信息
	 *
	 * @param roleIds
	 */
	void deleteBatchRoleMenuByRoleIds(Long[] roleIds);

}
