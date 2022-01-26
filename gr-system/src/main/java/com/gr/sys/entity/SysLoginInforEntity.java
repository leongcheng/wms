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
import java.util.Date;

/**
 * 登陆日志
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_login_infor")
@ApiModel(value="sys_login_infor对象", description="登陆日志")
public class SysLoginInforEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId
	@ApiModelProperty(value = "主键ID")
	private Long loginInforId;

	/**
	 *  登录账号
	 */
	@Excel(name = "登录账号", width = 30)
	@ApiModelProperty(value = "登录账号")
	private String account;

	/**
	 *  登录名称
	 */
	@Excel(name = "登录名称", width = 30)
	@ApiModelProperty(value = "登录名称")
	private String username;

	/**
	 *  登录IP
	 */
	@Excel(name = "登录IP", width = 20)
	@ApiModelProperty(value = "登录IP")
	private String ipaddr;

	/**
	 *  登录地点
	 */
	@Excel(name = "登录地点", width = 25)
	@ApiModelProperty(value = "登录地点")
	private String location;

	/**
	 *  浏览器类型
	 */
	@Excel(name = "浏览器", width = 25)
	@ApiModelProperty(value = "浏览器")
	private String browser;
	/**
	 *  操作系统
	 */
	@Excel(name = "操作系统", width = 25)
	@ApiModelProperty(value = "操作系统")
	private String os;

	/**
	 * 登录状态（0成功 1失败）
	 */
	@Excel(name = "登录状态", replace = {"成功_0", "失败_1"})
	@ApiModelProperty(value = "登录状态")
	private Integer status;

	/**
	 * 提示消息
	 */
	@Excel(name = "提示消息", width = 25, isWrap = true)
	@ApiModelProperty(value = "提示消息")
	private String message;

	/**
	 * 移动平台(0) 电脑设备(1)
	 */
	@Excel(name = "登录设备", replace = {"移动平台_0", "电脑设备_1"})
	@ApiModelProperty(value = "登录设备")
	private Integer mobile;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

}
