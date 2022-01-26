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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_config")
@ApiModel(value="sys_config对象", description="系统配置")
public class SysConfigEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId
	private Long configId;

	/**
	 * 参数名称
	 */
	@Excel(name = "角色类型", width = 20)
	@NotBlank(message = "参数名称不能为空")
	@ApiModelProperty(value = "参数名称")
	private String configName;

	/**
	 * 参数名
	 */
	@Excel(name = "角色类型", width = 20)
	@NotBlank(message = "参数键名不能为空")
	@ApiModelProperty(value = "参数名")
	private String paramKey;

	/**
	 * 参数值
	 */
	@Excel(name = "参数值", width = 20)
	@NotBlank(message = "参数键值不能为空")
	@ApiModelProperty(value = "参数值")
	private String paramValue;

	/**
	 * 备注信息
	 */
	@Excel(name = "备注信息", width = 20)
	@ApiModelProperty(value = "备注信息")
	private String remark;

	/**
	 * 状态 0.停用 1.启用
	 */
	@Excel(name = "状态",  replace = {"作废_-1", "启用_0", "停用_1"})
	@ApiModelProperty(value = "状态")
	private Integer status;

	/**
	 * 创建时间
	 */
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ApiModelProperty(value = "更新时间")
	private LocalDateTime updateTime;

}
