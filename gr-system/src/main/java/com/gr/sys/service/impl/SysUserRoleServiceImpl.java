package com.gr.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysUserRoleDao;
import com.gr.sys.entity.SysUserRoleEntity;
import com.gr.sys.service.SysUserRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRoleEntity> implements SysUserRoleService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String information = (String) params.get("information");
		IPage<SysUserRoleEntity> page = this.page(
				new Query<SysUserRoleEntity>().getPage(params),
				new QueryWrapper<SysUserRoleEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveOrUpdateSysUserRole(Long userId, List<Long> roleIdList) {

		// 先删除用户与角色关系
		deleteSysUserRoleByUserId(userId);

		if (CollectionUtils.isEmpty(roleIdList)){
			return true;
		}
		// 保存用户与角色关系
		List<SysUserRoleEntity> list = new ArrayList<>(roleIdList.size());
		for (Long roleId : roleIdList) {
			SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
			sysUserRoleEntity.setUserId(userId);
			sysUserRoleEntity.setRoleId(roleId);

			list.add(sysUserRoleEntity);
		}
		return this.saveBatch(list);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteSysUserRoleByRoleId(Long roleId) {

		return this.remove(new QueryWrapper<SysUserRoleEntity>().eq("role_id", roleId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteSysUserRoleByUserId(Long userId) {

		return this.remove(new QueryWrapper<SysUserRoleEntity>().eq("user_id", userId));
	}

}
