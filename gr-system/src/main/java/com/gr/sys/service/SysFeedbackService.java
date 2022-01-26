package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysFeedbackEntity;

import java.util.Map;

/**
 * 意见反馈表
 *
 * @author liangc
 * @date 2019-09-09 10:15:23
 */
public interface SysFeedbackService extends IService<SysFeedbackEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

