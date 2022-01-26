package com.gr.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.quartz.entity.ScheduleJobEntity;
import com.gr.utils.PageUtils;

import java.util.Map;

/**
 * 定时任务
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
public interface ScheduleJobService extends IService<ScheduleJobEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存定时任务
	 */
	void saveJob(ScheduleJobEntity scheduleJob);
	
	/**
	 * 更新定时任务
	 */
	void update(ScheduleJobEntity scheduleJob);
	
	/**
	 * 批量删除定时任务
	 */
	void deleteBatch(Long[] jobIds);
	
	/**
	 * 批量更新定时任务状态
	 */
	int updateBatch(Long[] jobIds, int status);
	
	/**
	 * 立即执行
	 */
	void run(ScheduleJobEntity scheduleJob);
	
	/**
	 * 暂停运行
	 */
	void pause(ScheduleJobEntity scheduleJob);
	
	/**
	 * 恢复运行
	 */
	void resume(ScheduleJobEntity scheduleJob);

	/**
	 * 删除任务
	 */
	void delete(Long jobId);
}
