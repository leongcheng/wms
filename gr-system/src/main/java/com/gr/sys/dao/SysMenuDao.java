package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysMenuDao extends BaseMapper<SysMenuEntity> {

	/**
	 * 根据用户id获取菜单id列表
	 *
	 * @param userid
	 * @return
	 */
	List<Long> getMenuIdListByUserId(Long userid);

	/**
	 * 根据用户id及父节点，获取相应子节点菜单信息
	 *
	 * @param parentId
	 * @param userId
	 * @return
	 */
	List<SysMenuEntity> getMenuListByParentIdAndUserId(@Param(value = "parentId") Long parentId, @Param(value = "userId") Long userId);

	/**
	 * 根据用户id获取菜单信息
	 *
	 * @param userId
	 * @return
	 */
	List<SysMenuEntity> getMenuListByUserId(@Param(value = "userId") Long userId, @Param(value = "pageType") Long pageType);

	/**
	 * 根据角色d获取菜单信息
	 * @param roleId
	 * @return
	 */
    List<Long> getMenuListByRoleId(Long roleId);
}
