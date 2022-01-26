package com.gr.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.quartz.dao.ScheduleJobDao;
import com.gr.quartz.entity.ScheduleJobEntity;
import com.gr.quartz.service.ScheduleJobService;
import com.gr.quartz.utils.ScheduleUtils;
import com.gr.utils.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("scheduleJobService")
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobDao, ScheduleJobEntity> implements ScheduleJobService {

	@Qualifier("schedulerFactoryBean")
	@Autowired
	private Scheduler scheduler;

	/**
	 * 项目启动时，初始化定时器
	 */
	@PostConstruct
	public void init(){
		List<ScheduleJobEntity> scheduleJobList = this.list();
		for(ScheduleJobEntity scheduleJob : scheduleJobList){
			CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
			//如果不存在，则创建
			if(cronTrigger == null) {
				ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
			}else {
				ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
			}
		}
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String information = (String)params.get("information");

		IPage<ScheduleJobEntity> page = this.page(
				new Query<ScheduleJobEntity>().getPage(params),
				new QueryWrapper<ScheduleJobEntity>().like(StringUtils.isNotBlank(information),"bean_name", information)
						.or().like(StringUtils.isNotBlank(information),"params", information)
						.or().like(StringUtils.isNotBlank(information),"remark", information)
						.orderByDesc("create_time")
		);

		return new PageUtils(page);
	}

	/**
	 * 新建定时任务
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveJob(ScheduleJobEntity scheduleJob) {
		scheduleJob.setCreateTime(new Date());
		//scheduleJob.setStatus(TypeUtils.ZERO);

		this.save(scheduleJob);

		ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
	}

	/**
	 * 更新定时任务
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(ScheduleJobEntity scheduleJob) {
		ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);

		this.updateById(scheduleJob);
	}

	/**
	 * 批量删除定时任务
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(Long[] jobIds) {
		for(Long jobId : jobIds){
			ScheduleUtils.deleteScheduleJob(scheduler, jobId);
		}

		//删除数据
		this.removeByIds(Arrays.asList(jobIds));
	}

	/**
	 * 删除定时任务
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long jobId) {

		ScheduleUtils.deleteScheduleJob(scheduler, jobId);
		//删除数据
		this.removeById(jobId);
	}

	/**
	 * 批量更新定时任务状态
	 */
	@Override
	public int updateBatch(Long[] jobIds, int status){
		Map<String, Object> map = new HashMap<>(2);
		map.put("list", jobIds);
		map.put("status", status);
		return baseMapper.updateBatch(map);
	}

	/**
	 * 立即执行任务
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void run(ScheduleJobEntity scheduleJob) {
		ScheduleUtils.run(scheduler, scheduleJob);
	}

	/**
	 * 暂停定时任务
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void pause(ScheduleJobEntity scheduleJob) {
		ScheduleUtils.pauseJob(scheduler, scheduleJob.getJobId());

		this.updateById(scheduleJob);
		//updateBatch(jobIds, Constant.ScheduleStatus.PAUSE.getValue());
	}

	/**
	 * 恢复定时任务
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void resume(ScheduleJobEntity scheduleJob) {
		ScheduleUtils.resumeJob(scheduler, scheduleJob.getJobId());

		this.updateById(scheduleJob);
		//updateBatch(jobIds, Constant.ScheduleStatus.NORMAL.getValue());
	}

}
