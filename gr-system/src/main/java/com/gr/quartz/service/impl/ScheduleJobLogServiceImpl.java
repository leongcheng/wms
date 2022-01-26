package com.gr.quartz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Constants;
import com.gr.constant.Query;
import com.gr.quartz.dao.ScheduleJobLogDao;
import com.gr.quartz.entity.ScheduleJobLogEntity;
import com.gr.quartz.service.ScheduleJobLogService;
import com.gr.utils.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;


@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogDao, ScheduleJobLogEntity> implements ScheduleJobLogService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String information = (String)params.get("information");
		String status = (String) params.get("status");
		String startDate = (String) params.get("startDate");
		String endDate = (String) params.get("endDate");

		QueryWrapper<ScheduleJobLogEntity> wrapper = new QueryWrapper<>();

		if (!StringUtils.isBlank(status)) {
			wrapper.eq("status", status);
		}
		// 起始时间
		wrapper.apply(StringUtils.isNotBlank(startDate),"DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
				" >= DATE_FORMAT( '" + startDate + "', '%Y-%m-%d' )");
		// 截止时间
		wrapper.apply(StringUtils.isNotBlank(endDate),"DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
				" <= DATE_FORMAT( '" + endDate + "', '%Y-%m-%d' )");

		if (!StringUtils.isBlank(information)) {
			wrapper.and(Wrapper -> Wrapper.like("job_id", information).or().like("bean_name", information));
		}
		wrapper.orderByDesc("create_time");

		IPage<ScheduleJobLogEntity> page = this.page(new Query<ScheduleJobLogEntity>().getPage(params), wrapper);

		return new PageUtils(page);
	}

	/**
	 * 记录mq发送消息
	 * @param parmas
	 * @param message
	 * @param orderId
	 */
	@Override
	public ScheduleJobLogEntity changeBrokerMessageLogSave(Map<String, Object> parmas, String message, String orderId){
		ScheduleJobLogEntity logJob = new ScheduleJobLogEntity();

		logJob.setJobId(Long.valueOf(Constants.SUPER_ADMIN))
				.setType(JSONObject.toJSONString(parmas))
				.setRemark(orderId)
				.setStatus(Constants.TWO)
				.setBeanName("rabbitmqTask")
				.setParams(message)
				.setCreateTime(LocalDateTime.now());

		this.save(logJob);

		return logJob;
	}

	/**
	 * mq消息确定状态更新
	 * @param messageId
	 */
	@Override
	public void changeBrokerMessageLogStatus(String messageId) {
		ScheduleJobLogEntity logJob = new ScheduleJobLogEntity();
		logJob.setStatus(0);

		LambdaQueryWrapper<ScheduleJobLogEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ScheduleJobLogEntity::getLogId, messageId);

		this.update(logJob, wrapper);
	}

}
