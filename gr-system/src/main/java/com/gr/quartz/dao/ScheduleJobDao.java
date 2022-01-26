package com.gr.quartz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.quartz.entity.ScheduleJobEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 定时任务
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
@Mapper
public interface ScheduleJobDao extends BaseMapper<ScheduleJobEntity> {
	
	/**
	 * 批量更新状态
	 */
	int updateBatch(Map<String, Object> map);
}
