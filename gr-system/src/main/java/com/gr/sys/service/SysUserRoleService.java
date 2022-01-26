package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysUserRoleEntity;

import java.util.List;
import java.util.Map;

/**
 * 用户角色对照表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysUserRoleService extends IService<SysUserRoleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存或更新 用户与权限对照信息
     * @param userId
     * @param roleIdList
     * @return
     */
    boolean saveOrUpdateSysUserRole(Long userId, List<Long> roleIdList);
    /**
     * 根据权限id删除用户权限对照信息
     * @param roleId
     * @return
     */
	boolean deleteSysUserRoleByRoleId(Long roleId);
	/**
	 * 根据用户id删除用户权限对照信息
	 * @param userId
	 * @return
	 */
	boolean deleteSysUserRoleByUserId(Long userId);

}

