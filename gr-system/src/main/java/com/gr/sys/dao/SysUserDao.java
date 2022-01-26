package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统用户表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUserEntity> {

	/**
	 * 根据用户userId查询用户权限授权列表
	 * @param userId
	 * @return
	 */
	List<String> queryAllPerms(Long userId);

	/**
	 * 根据用户表id获取用户信息（包括权限id列表）
	 * @param userId
	 * @return
	 */
	SysUserEntity getSysUserInfo(Long userId);
}
