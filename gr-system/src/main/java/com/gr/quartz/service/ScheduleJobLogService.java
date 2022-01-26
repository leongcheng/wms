package com.gr.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.quartz.entity.ScheduleJobLogEntity;
import com.gr.utils.PageUtils;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
public interface ScheduleJobLogService extends IService<ScheduleJobLogEntity> {

	PageUtils queryPage(Map<String, Object> params);

	ScheduleJobLogEntity changeBrokerMessageLogSave(Map<String, Object> parmas, String message, String orderId);

	void changeBrokerMessageLogStatus(String messageId);
}
