package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysLoginInforEntity;

import java.util.Map;

/**
 * 系统访问记录
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysLoginInforService extends IService<SysLoginInforEntity> {

    PageUtils queryPage(Map<String, Object> params);

}

