package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysUserPostEntity;

import java.util.List;
import java.util.Map;

/**
 * 用户与岗位关联表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysUserPostService extends IService<SysUserPostEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean saveOrUpdateUserPost(Long userId, List<Long> postIdList);

    SysUserPostEntity loginUserPost(Long userId);
}

