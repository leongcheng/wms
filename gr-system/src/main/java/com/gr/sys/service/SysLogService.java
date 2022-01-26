package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysLogEntity;

import java.util.Map;

/**
 * 操作日志
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveLog(SysLogEntity sysLog);

}

