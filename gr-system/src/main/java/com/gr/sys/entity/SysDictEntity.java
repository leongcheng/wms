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
 * 数据字典
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_dict")
@ApiModel(value="sys_dict对象", description="据字典")
public class SysDictEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 字典id
	 */
	@TableId
	private Long dictId;

	/**
	 * 名称
	 */
	@Excel(name = "名称", width = 20)
	@ApiModelProperty(value = "名称")
	private String dictName;

	/**
	 * 编码
	 */
	@Excel(name = "编码", width = 20)
	@ApiModelProperty(value = "编码")
	private Long dictCode;

	/**
	 * 值
	 */
	@Excel(name = "值", width = 20)
	@ApiModelProperty(value = "值")
	private String value;

	/**
	 * 类型
	 */
	@Excel(name = "类型", width = 20)
	@ApiModelProperty(value = "类型")
	private String type;

	/**
	 * 状态，-1：删除 0：正常 1：停用
	 */
	@Excel(name = "状态",  replace = {"作废_-1", "启用_0", "停用_1"})
	@ApiModelProperty(value = "状态")
	private Integer status;

	/**
	 * 备注
	 */
	@Excel(name = "备注", width = 20)
	@ApiModelProperty(value = "备注")
	private String remark;

	/**
	 * 创建时间
	 */
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime updateTime;
}
