package com.gr.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Constants;
import com.gr.sys.dao.SysMenuDao;
import com.gr.sys.entity.SysMenuEntity;
import com.gr.sys.service.SysMenuService;
import com.gr.sys.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService
{
	@Autowired
	private SysRoleMenuService sysRoleMenuService;

	@Override
	public List<SysMenuEntity> getMenuListByParentId(Long parentId) {

		return this.list(new LambdaQueryWrapper<SysMenuEntity>().eq(SysMenuEntity::getParentId, parentId)
				.ne(SysMenuEntity::getStatus, 1)
				.orderByAsc(SysMenuEntity::getSort));
	}

	/**
	 * 根据用户id及父节点，获取相应子节点菜单信息
	 * @param parentId
	 * @param userId
	 * @return
	 */
	@Override
	public List<SysMenuEntity> getMenuListByParentIdAndUserId(Long parentId, Long userId) {

		if (userId == Constants.SUPER_ADMIN) {
			return this.getMenuListByParentId(parentId);
		}
		return baseMapper.getMenuListByParentIdAndUserId(parentId, userId);
	}

	/**
	 * PC端
	 */
	@Override
	public List<SysMenuEntity> getMenuListByUserId(Long userId) {
		// 查询根菜单列表
		List<SysMenuEntity> menuList = this.getMenuListByParentIdAndUserId(0L, userId);
		// 递归获取子菜单
		this.getMenuTreeList(menuList, userId);

		return menuList;
	}

	/**
	 * 手机端
	 */
	@Override
	public List<SysMenuEntity> getMenuListByUserIdMobile(Long userId) {

		return baseMapper.getMenuListByUserId(userId, 0L);
	}

	/**
	 * 递归获取层层目录下的菜单
	 */
	private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, Long userId) {
		List<SysMenuEntity> subMenuList = new ArrayList<>();
		for (SysMenuEntity entity : menuList) {
			// 目录
			if (entity.getType() == Constants.MenuType.CATALOG.getValue()) {
				entity.setChildren(getMenuTreeList(getMenuListByParentIdAndUserId(entity.getMenuId(), userId), userId));
			}
			subMenuList.add(entity);
		}
		return subMenuList;
	}

	/**
	 * 菜单列表
	 */
	@Override
	public List<SysMenuEntity> getAllMenuList() {
		//1.查出所有菜单
		List<SysMenuEntity> entities = this.list();

		//2.组装成父子的树状图结构
		List<SysMenuEntity> menuList = entities.stream()
				.filter(menuEntity -> menuEntity.getParentId() == 0)
				.peek(menu -> menu.setChildren(getChildrens(menu, entities)))
				.sorted(Comparator.comparingInt(SysMenuEntity::getSort)).collect(Collectors.toList());

		return menuList;
	}

	/**
	 * 递归寻找所有菜单的子菜单
	 * @param sysMenu
	 * @param menuList
	 * @return
	 */
	private List<SysMenuEntity> getChildrens(SysMenuEntity sysMenu, List<SysMenuEntity> menuList) {

		/*List<SysMenuEntity> children = menuList.stream()
				.filter(menuEntity -> menuEntity.getParentId() == sysMenu.getMenuId())
				.peek(menuEntity -> menuEntity.setChildren(getChildrens(menuEntity, menuList)))
				.sorted(Comparator.comparingInt(SysMenuEntity::getSort)).collect(Collectors.toList());*/

		List<SysMenuEntity> children = new ArrayList<>();
		for (SysMenuEntity menu : menuList) {
			if (sysMenu.getMenuId().equals(menu.getParentId())) {
				menu.setChildren(getChildrens(menu, menuList));
				children.add(menu);
			}
		}

		return children.stream().sorted(Comparator.comparingInt(SysMenuEntity::getSort)).collect(Collectors.toList());
	}


	/**
	 * 获取不包含按钮的菜单列表
	 */
	@Override
	public List<SysMenuEntity> queryNotButtonList() {
		List<SysMenuEntity> entities = this.list(new LambdaQueryWrapper<SysMenuEntity>().ne(SysMenuEntity::getType, Constants.TWO));

		//2.组装成父子的树状图结构
		List<SysMenuEntity> menuList = entities.stream()
				.filter(menuEntity -> menuEntity.getParentId() == 0)
				.peek(menu -> menu.setChildren(getChildrens(menu, entities)))
				.sorted(Comparator.comparingInt(SysMenuEntity::getSort)).collect(Collectors.toList());

		return menuList;
	}

	/**
	 * 根据角色ID查询菜单树信息
	 * @param roleId
	 * @return
	 */
	@Override
	public List<Long> selectMenuListByRoleId(Long roleId) {
		return baseMapper.getMenuListByRoleId(roleId);
	}


	/**
	 * 删除菜单Id
	 * @param menuId
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteByMenuId(Long menuId) {
		sysRoleMenuService.deleteByMenuId(menuId);

		return this.removeById(menuId);
	}

}
