package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysUserEntity;

import java.util.Map;

/**
 * 系统用户表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysUserService extends IService<SysUserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
	 * 修改密码
	 *
	 * @param userId 用户ID
	 * @param password  原密码
	 * @param newPassword 新密码
	 */
	boolean updateUserPassword(Long userId, String password, String newPassword);

	/**
	 * 保存用户
	 *
	 * @param user
	 * @return
	 */
	boolean saveUser(SysUserEntity user);

	/**
	 * 修改用户
	 *
	 * @param user
	 * @return
	 */
	boolean updateUser(SysUserEntity user);

	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	SysUserEntity getSysUserInfo(Long userId);

	/**
	 * 删除用户
	 * @param userIds
	 */
	boolean deleteUserIds(Long[] userIds);
}

