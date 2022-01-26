package com.gr.quartz.utils;

import com.gr.constant.Constants;
import com.gr.quartz.entity.ScheduleJobEntity;
import com.gr.quartz.entity.ScheduleJobLogEntity;
import com.gr.quartz.service.ScheduleJobLogService;
import com.gr.utils.SpringContextUtils;
import com.gr.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


/**
 * 定时任务
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
@Slf4j
@DisallowConcurrentExecution // 禁止并发执行
public class ScheduleJob extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext context) {

		ScheduleJobEntity scheduleJob = new ScheduleJobEntity();
		//通过拷贝类实现
		BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleJobEntity.JOB_PARAM_KEY), scheduleJob);

		//保存执行记录
		ScheduleJobLogEntity logJob = new ScheduleJobLogEntity();
		logJob.setJobId(scheduleJob.getJobId());
		logJob.setBeanName(scheduleJob.getBeanName());
		logJob.setRemark(scheduleJob.getRemark());
		logJob.setParams(scheduleJob.getParams());
		logJob.setCreateTime(LocalDateTime.now());
		//任务开始时间
		long startTime = System.currentTimeMillis();

		try {
			//执行任务
			log.debug("任务准备执行，任务ID：" + scheduleJob.getJobId());

			Object target = SpringContextUtils.getBean(scheduleJob.getBeanName());
			Method method = target.getClass().getDeclaredMethod("run", String.class);
			method.invoke(target, scheduleJob.getParams());

			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			logJob.setTimes(times);
			//任务状态 0：成功 1：失败
			logJob.setStatus(Constants.ZERO);

			log.debug("任务执行完毕，任务ID：" + scheduleJob.getJobId() + "  总共耗时：" + times + "毫秒");
		} catch (Exception e) {
			//任务执行总时长
			log.error("任务执行失败，任务ID{}" + scheduleJob.getJobId(), e.getMessage());

			long times = System.currentTimeMillis() - startTime;
			logJob.setTimes(times);

			//任务状态 0：成功 1：失败
			logJob.setStatus(Constants.SUPER_ADMIN);
			logJob.setError(StringUtils.substring(e.toString(), 0, 2000));
		}finally {
			logJob.setUpdateTime(LocalDateTime.now());

			SpringUtil.getBean(ScheduleJobLogService.class).save(logJob);
		}
	}
}
