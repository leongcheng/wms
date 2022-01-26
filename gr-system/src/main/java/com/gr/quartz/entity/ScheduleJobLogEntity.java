package com.gr.quartz.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 定时任务日志
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("schedule_job_log")
public class ScheduleJobLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 日志id
	 */
	@TableId
	private Long logId;

	/**
	 * 任务id
	 */
	@Excel(name = "任务ID", width = 20)
	private Long jobId;

	/**
	 * spring bean名称
	 */
	@Excel(name = "任务名称", width = 20)
	private String beanName;

	/**
	 * 参数
	 */
	@Excel(name = "参数", width = 20)
	private String params;

	/**
	 * 备注
	 */
	@Excel(name = "备注", width = 20)
	private String remark;

	/**
	 * 任务状态    0：成功    1：失败   2:执行中
	 */
	@Excel(name = "状态", replace = {"成功_0", "失败_1"})
	private Integer status;

	/**
	 * 执行计数
	 */
	private int count;

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 失败信息
	 */
	@Excel(name = "失败信息", width = 20)
	private String error;

	/**
	 * 执行时长(毫秒)
	 */
	@Excel(name = "执行时长(毫秒)", width = 20)
	private Long times;

	/**
	 * 超时时长
	 */
	private Date nextRetry;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;
}
