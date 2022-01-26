package com.gr.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Constants;
import com.gr.constant.Query;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysRoleDao;
import com.gr.sys.entity.SysRoleEntity;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.service.SysRoleMenuService;
import com.gr.sys.service.SysRoleService;
import com.gr.sys.service.SysUserRoleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;


@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {

	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysUserRoleService sysUserRoleService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String information = (String) params.get("information");

		SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();

		IPage<SysRoleEntity> page = this.page(
				new Query<SysRoleEntity>().getPage(params),
				new QueryWrapper<SysRoleEntity>()
						.like(StringUtils.isNotBlank(information), "role_name", information)
						.inSql(user.getUserId() != Constants.SUPER_ADMIN,"role_id", "SELECT sud.role_id FROM sys_user_role sud WHERE user_id =" + user.getUserId())
						.ne(user.getUserId() != Constants.SUPER_ADMIN,"role_id", 1)
		);

		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveSysRole(SysRoleEntity sysRole) {
		SysUserEntity loginUser = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
		sysRole.setCreateTime(LocalDateTime.now()).setUpdateTime(sysRole.getCreateTime());
		sysRole.setCreator(loginUser.getUsername());

		this.save(sysRole);

		// 保存权限角色与菜单关系
		return sysRoleMenuService.saveOrUpdateSysRoleMenu(sysRole.getRoleId(), sysRole.getMenuIdList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSysRole(SysRoleEntity sysRole) {
		this.updateById(sysRole);

		// 保存权限角色与菜单关系
		return sysRoleMenuService.saveOrUpdateSysRoleMenu(sysRole.getRoleId(), sysRole.getMenuIdList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteSysRoleByRoleId(Long[] roleIds) {
		for (int i = 0; i < roleIds.length; i++) {
			// 删除权限与用户关联
			sysUserRoleService.deleteSysUserRoleByRoleId(roleIds[i]);

			// 删除权限与菜单关联
			sysRoleMenuService.deleteSysRoleMenuByRoleId(roleIds[i]);
		}

		// 最后删除权限信息
		return this.removeByIds(Arrays.asList(roleIds));
	}

}
