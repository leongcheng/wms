package com.gr.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gr.quartz.entity.ScheduleJobLogEntity;
import com.gr.quartz.service.ScheduleJobLogService;
import com.gr.sys.entity.SysFileEntity;
import com.gr.sys.entity.SysLogEntity;
import com.gr.sys.entity.SysLoginInforEntity;
import com.gr.sys.service.SysFileService;
import com.gr.sys.service.SysLogService;
import com.gr.sys.service.SysLoginInforService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 定时任务
 *
 * @author liangc
 * @date 2021-03-20 11:14:32
 */
@Slf4j
@Component("logTask")
public class LogTask implements ITask
{
	@Autowired
	private SysFileService sysFileService;
	@Autowired
	private SysLogService sysLogService;
	@Autowired
	private SysLoginInforService sysLoginInforService;
	@Autowired
	private ScheduleJobLogService scheduleJobLogService;

	/**
	 * 日志清理
	 * @param params   参数，多参数使用JSON数据
	 */
	@Override
	public void run(String params) {
		try {
			LocalDate dateTime = LocalDate.now();

			//操作日志(保留3年)
			List<SysLogEntity> sysLogEntities = sysLogService.list(new QueryWrapper<SysLogEntity>()
					.apply("DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
							" >= DATE_FORMAT( '" + dateTime.minusDays(356*4) + "', '%Y-%m-%d' )")
					.apply("DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
							" <= DATE_FORMAT( '" + dateTime.minusDays(356*3) + "', '%Y-%m-%d' )")
			);

			// 删除（根据ID 批量删除）
			List<Long> courseIds1 = sysLogEntities.stream().map(SysLogEntity::getLogId).collect(Collectors.toList());
			sysLogService.removeByIds(courseIds1);

			//登录日志(保留365天)
			List<SysLoginInforEntity> sysLoginInforEntities = sysLoginInforService.list(new QueryWrapper<SysLoginInforEntity>()
					.apply("DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
							" >= DATE_FORMAT( '" + dateTime.minusDays(1000) + "', '%Y-%m-%d' )")
					.apply("DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
							" <= DATE_FORMAT( '" + dateTime.minusDays(356) + "', '%Y-%m-%d' )")
			);

			// 删除（根据ID 批量删除）
			List<Long> courseIds2 = sysLoginInforEntities.stream().map(SysLoginInforEntity::getLoginInforId).collect(Collectors.toList());
			sysLoginInforService.removeByIds(courseIds2);

			//定时任务日志(保留3天)
			List<ScheduleJobLogEntity> scheduleJobLogEntities = scheduleJobLogService.list(new QueryWrapper<ScheduleJobLogEntity>()
					.apply("DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
							" >= DATE_FORMAT( '" + dateTime.minusDays(90) + "', '%Y-%m-%d' )")
					.apply("DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
							" <= DATE_FORMAT( '" + dateTime.minusDays(3) + "', '%Y-%m-%d' )")
			);

			// 删除（根据ID 批量删除）
			List<Long> courseIds3 = scheduleJobLogEntities.stream().map(ScheduleJobLogEntity::getLogId).collect(Collectors.toList());
			scheduleJobLogService.removeByIds(courseIds3);

			// 删除无用文件
			List<SysFileEntity> fileList = sysFileService.list(new LambdaQueryWrapper<SysFileEntity>().isNull(SysFileEntity::getFileId));
			fileList.forEach(file -> {
				sysFileService.deleteFile(file.getFileId());
			});

		} catch (Exception e) {
			log.error("日志清理任务失败{}", e.getMessage());
		}
	}
}
