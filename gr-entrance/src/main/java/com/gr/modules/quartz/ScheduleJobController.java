package com.gr.modules.quartz;

import com.gr.annotation.SysLog;
import com.gr.constant.R;
import com.gr.quartz.entity.ScheduleJobEntity;
import com.gr.quartz.service.ScheduleJobService;
import com.gr.utils.PageUtils;
import com.gr.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 定时任务
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
@RestController
@RequestMapping("/sys/schedule")
public class ScheduleJobController
{
	@Autowired
	private ScheduleJobService scheduleJobService;

	/**
	 * 定时任务列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:schedule:list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = scheduleJobService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 定时任务信息
	 */
	@RequestMapping("/info/{jobId}")
	@RequiresPermissions("sys:schedule:info")
	public R info(@PathVariable("jobId") Long jobId){
		ScheduleJobEntity schedule = scheduleJobService.getById(jobId);

		return R.ok().put("schedule", schedule);
	}

	/**
	 * 保存定时任务
	 */
	@SysLog("保存定时任务")
	@RequestMapping("/save")
	@RequiresPermissions("sys:schedule:save")
	public R save(@RequestBody ScheduleJobEntity scheduleJob){
		ValidatorUtils.validateEntity(scheduleJob);

		scheduleJobService.saveJob(scheduleJob);

		return R.ok();
	}

	/**
	 * 修改定时任务
	 */
	@SysLog("修改定时任务")
	@RequestMapping("/update")
	@RequiresPermissions("sys:schedule:update")
	public R update(@RequestBody ScheduleJobEntity scheduleJob){
		ValidatorUtils.validateEntity(scheduleJob);

		scheduleJobService.update(scheduleJob);

		return R.ok();
	}

	/**
	 * 删除定时任务
	 */
	@SysLog("删除定时任务")
	@RequestMapping("/delete")
	@RequiresPermissions("sys:schedule:delete")
	public R delete(@RequestBody Long [] jobIds){
		scheduleJobService.deleteBatch(jobIds);

		return R.ok();
	}

	/**
	 * 立即执行任务
	 */
	@SysLog("立即执行任务")
	@RequestMapping("/run")
	@RequiresPermissions("sys:schedule:run")
	public R run(@RequestBody ScheduleJobEntity scheduleJob){
		scheduleJobService.run(scheduleJob);

		return R.ok();
	}

	/**
	 * 暂停定时任务
	 */
	@SysLog("暂停定时任务")
	@RequestMapping("/pause")
	@RequiresPermissions("sys:schedule:pause")
	public R pause(@RequestBody Long [] jobIds){

		List<ScheduleJobEntity> listByIds = scheduleJobService.listByIds(Arrays.asList(jobIds));
		listByIds.forEach(scheduleJob->{
			scheduleJobService.pause(scheduleJob.setStatus(1));
		});

		return R.ok();
	}

	/**
	 * 恢复定时任务
	 */
	@SysLog("恢复定时任务")
	@RequestMapping("/resume")
	@RequiresPermissions("sys:schedule:resume")
	public R resume(@RequestBody Long [] jobIds){

		List<ScheduleJobEntity> listByIds = scheduleJobService.listByIds(Arrays.asList(jobIds));
		listByIds.forEach(scheduleJob->{
			scheduleJobService.resume(scheduleJob.setStatus(0));
		});

		return R.ok();
	}

}
