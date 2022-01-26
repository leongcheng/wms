package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysPostEntity;

import java.util.Map;

/**
 * 岗位信息表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysPostService extends IService<SysPostEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

