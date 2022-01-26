package com.gr.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysRoleMenuDao;
import com.gr.sys.entity.SysRoleMenuEntity;
import com.gr.sys.service.SysRoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("sysRoleMenuService")
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuDao, SysRoleMenuEntity> implements SysRoleMenuService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String information = (String) params.get("information");

		IPage<SysRoleMenuEntity> page = this.page(
				new Query<SysRoleMenuEntity>().getPage(params),
				new QueryWrapper<SysRoleMenuEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<Long> getRoleMenuIdListByRoleId(Long roleId) {

		return baseMapper.getRoleMenuIdListByRoleId(roleId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveOrUpdateSysRoleMenu(Long roleId, List<Long> menuIdList) {
		// 先删除权限与菜单关系
		this.remove(new QueryWrapper<SysRoleMenuEntity>().eq("role_id", roleId));

		if (menuIdList == null || menuIdList.size() == 0) {
			return true;
		}
		// 保存权限与菜单关系
		List<SysRoleMenuEntity> list = new ArrayList<>(menuIdList.size());
		for (Long menuId : menuIdList) {
			SysRoleMenuEntity sysRoleMenu = new SysRoleMenuEntity();
			sysRoleMenu.setMenuId(menuId);
			sysRoleMenu.setRoleId(roleId);
			list.add(sysRoleMenu);
		}
		return this.saveBatch(list);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteSysRoleMenuByRoleId(Long roleId) {

		return this.remove(new QueryWrapper<SysRoleMenuEntity>().eq("role_id", roleId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteByMenuId(Long menuId) {
		return this.remove(new QueryWrapper<SysRoleMenuEntity>().eq("menu_id", menuId));
	}

}
