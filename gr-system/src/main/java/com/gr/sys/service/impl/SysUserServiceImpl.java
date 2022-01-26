package com.gr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Constants;
import com.gr.constant.Query;
import com.gr.exception.ResultEnum;
import com.gr.exception.ResultException;
import com.gr.utils.PageUtils;
import com.gr.utils.VerifierUtils;
import com.gr.utils.ShiroUtils;
import com.gr.sys.dao.SysUserDao;
import com.gr.sys.entity.SysPostEntity;
import com.gr.sys.entity.SysRoleEntity;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {

	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysUserPostService sysUserPostService;
	@Autowired
	private SysPostService sysPostService;
	@Autowired
	private SysRoleService sysRoleService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String information = (String)params.get("information");
		String status = (String)params.get("status");

		SysUserEntity sysUser = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();

		IPage<SysUserEntity> page = this.page(
				new Query<SysUserEntity>().getPage(params),
				new QueryWrapper<SysUserEntity>()
						.like(StringUtils.isNotBlank(information),"account", information)
						.or()
						.like(StringUtils.isNotBlank(information), "username", information)
						.or()
						.like(StringUtils.isNotBlank(information), "post_name", information)
						.or()
						.like(StringUtils.isNotBlank(information), "phone", information)
						.or()
						.like(StringUtils.isNotBlank(information), "email", information)
						.or()
						.like(StringUtils.isNotBlank(information), "company", information)
						.eq(StringUtils.isNotBlank(status),"status", status)
						.ne(sysUser.getUserId() !=  Constants.SUPER_ADMIN,"user_id", sysUser.getUserId())
						.ne("deleted", Constants.SUPER_ADMIN)
						.orderByDesc("update_time")
		);

		return new PageUtils(page);
	}

	/**
	 * 修改密码
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateUserPassword(Long userId, String password, String newPassword) {
		SysUserEntity userEntity = new SysUserEntity();

		userEntity.setPassword(newPassword);

		return this.update(userEntity,
				new LambdaQueryWrapper<SysUserEntity>()
						.eq(SysUserEntity::getUserId, userId)
						.eq(SysUserEntity::getPassword, password));
	}


	/**
	 * 新增用户
	 * @param user
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveUser(SysUserEntity user) {
		// 用户唯一性检查
		if(uniqueness(user)){
			throw new ResultException(ResultEnum.ERR001);
		}
		// 角色、岗位信息
		savePostRole(user);

		SysUserEntity sysUser = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setSalt(salt)
				.setPassword(ShiroUtils.sha256(user.getAccount().toUpperCase(), user.getSalt()))
				.setCreateTime(LocalDateTime.now())
				.setUpdateTime(user.getCreateTime())
				.setCreator(sysUser.getUsername());

		if(this.save(user)){
			//保存岗位与用户关系
			sysUserPostService.saveOrUpdateUserPost(user.getUserId(), user.getPostIdList());
			//保存用户与角色关系
			return sysUserRoleService.saveOrUpdateSysUserRole(user.getUserId(), user.getRoleIdList());
		}

		return false;
	}


	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateUser(SysUserEntity user) {
		// 角色、岗位信息
		savePostRole(user);

		SysUserEntity sysUser = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();

		user.setPassword(StringUtils.isBlank(user.getPassword()) ? null : ShiroUtils.sha256(user.getPassword(), user.getSalt()));
		user.setCreator(sysUser.getUsername()).setUpdateTime(LocalDateTime.now());

		if (this.updateById(user)) {
			//更新岗位与用户关系
			sysUserPostService.saveOrUpdateUserPost(user.getUserId(), user.getPostIdList());
			//更新角色与用户关系
			return sysUserRoleService.saveOrUpdateSysUserRole(user.getUserId(), user.getRoleIdList());
		}
		return false;
	}

	/**
	 * 角色岗位信息
	 * @param user
	 */
	private void savePostRole(SysUserEntity user) {
		//手机号验证
		if(StringUtils.isNotBlank(user.getPhone())){
			VerifierUtils.isPhoneLegal(user.getPhone());
		}
		if (CollectionUtils.isEmpty(user.getRoleIdList())){
			throw new ResultException(ResultEnum.ERR002);
		}
		if (CollectionUtils.isEmpty(user.getPostIdList()) || user.getPostIdList().size() > 1){
			throw new ResultException(ResultEnum.ERR003);
		}

		// 岗位ID
		Iterator<Long> it = user.getPostIdList().iterator();
		while (it.hasNext()) {
			SysPostEntity post = sysPostService.getOne(new LambdaQueryWrapper<SysPostEntity>().eq(SysPostEntity::getPostId, it.next()));
			user.setPostName(post.getPostName());
			user.setPostId(post.getPostId());
		}

		// 角色名称
		Iterator<Long> iterator = user.getRoleIdList().iterator();
		String roleName = "";
		while (iterator.hasNext()) {
			SysRoleEntity role = sysRoleService.getOne(new LambdaQueryWrapper<SysRoleEntity>().eq(SysRoleEntity::getRoleId, iterator.next()));
			if(!iterator.hasNext()){
				roleName += role.getRoleName();
			}else {
				roleName += role.getRoleName() + "/";
			}
		}
		user.setRoleName(roleName);
	}


	/**
	 * 删除用户
	 * @param userIds
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteUserIds(Long[] userIds) {
		SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();

		if (ArrayUtils.contains(userIds, 1L))
			throw new ResultException(ResultEnum.ERR004);

		if (ArrayUtils.contains(userIds, user.getUserId()))
			throw new ResultException(ResultEnum.ERR005);

		// 删除关联信息
		for (int i = 0; i < userIds.length; i++) {

			//删除岗位与用户关系
			sysUserPostService.saveOrUpdateUserPost(userIds[i], null);
			//删除角色与用户关系
			sysUserRoleService.saveOrUpdateSysUserRole(userIds[i], null);
		}

		return this.removeByIds(Arrays.asList(userIds));
	}


	/**
	 * 唯一性检查
	 * @param user
	 * @return
	 */
	public boolean uniqueness(SysUserEntity user){

		long count = this.count(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getAccount, user.getAccount()));

		return count > 0 ? true : false;
	}



	@Override
	public SysUserEntity getSysUserInfo(Long userId) {

		return baseMapper.getSysUserInfo(userId);
	}

}
