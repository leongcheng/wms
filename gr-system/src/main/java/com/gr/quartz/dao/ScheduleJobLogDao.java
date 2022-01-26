package com.gr.quartz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.quartz.entity.ScheduleJobLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
@Mapper
public interface ScheduleJobLogDao extends BaseMapper<ScheduleJobLogEntity> {
	
}
