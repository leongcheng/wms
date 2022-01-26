package com.gr.sys.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_log")
@ApiModel(value="sys_log对象", description="操作日志")
public class SysLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 表id
	 */
	@TableId
	@ApiModelProperty(value = "主键ID")
	private Long logId;

	/**
	 * 用户名
	 */
	@Excel(name = "用户名", width = 20)
	@ApiModelProperty(value = "用户名")
	private String username;

	/**
	 * 登录账号
	 */
	@Excel(name = "登录账号", width = 20)
	@ApiModelProperty(value = "登录账号")
	private String account;

	/**
	 * 用户操作
	 */
	@Excel(name = "用户操作", width = 20)
	@ApiModelProperty(value = "用户操作")
	private String operation;

	/**
	 * 请求方法
	 */
	@Excel(name = "请求方法", width = 40)
	@ApiModelProperty(value = "请求方法")
	private String method;

	/**
	 * 请求参数
	 */
	@ApiModelProperty(value = "请求参数")
	private String params;

	/**
	 * 状态
	 */
	@Excel(name = "状态", replace = {"成功_0", "失败_1"})
	@ApiModelProperty(value = "状态")
	public int status;

	/**
	 * 错误信息
	 */
	@Excel(name = "错误信息", width = 20)
	@ApiModelProperty(value = "错误信息")
	public String message;

	/**
	 * 执行时长(毫秒)
	 */
	@Excel(name = "执行时长(毫秒)", width = 20)
	@ApiModelProperty(value = "执行时长(毫秒)")
	private Long time;

	/**
	 *  IP地址
	 */
	@Excel(name = "IP地址", width = 20)
	@ApiModelProperty(value = "IP地址")
	private String ipaddr;

	/**
	 * 操作地点
	 */
	@Excel(name = "操作地点", width = 20)
	@ApiModelProperty(value = "操作地点")
	private String location;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;
}
