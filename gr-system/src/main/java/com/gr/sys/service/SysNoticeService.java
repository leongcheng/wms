package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysNoticeEntity;

import java.util.Map;

/**
 * 站内消息
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysNoticeService extends IService<SysNoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);

}

