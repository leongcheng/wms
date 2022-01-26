package com.gr.sys.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gr.validator.group.AddGroup;
import com.gr.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 岗位信息表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_post")
@ApiModel(value="sys_post对象", description="岗位管理")
public class SysPostEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 岗位ID
	 */
	@TableId
	private Long postId;

	/**
	 * 岗位编码
	 */
	@Excel(name = "岗位编码", width = 20)
	@NotBlank(message = "岗位编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "岗位编码")
	private String postCode;

	/**
	 * 岗位名称
	 */
	@Excel(name = "岗位名称", width = 20)
	@NotBlank(message = "岗位名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "岗位名称")
	private String postName;

	/**
	 * 显示顺序
	 */
	@Excel(name = "显示顺序", width = 20)
	@NotNull(message = "显示顺序不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "显示顺序")
	private Integer postSort;

	/**
	 * 备注
	 */
	@Excel(name = "备注", width = 20)
	@ApiModelProperty(value = "备注")
	private String remark;

	/**
	 * 状态，-1：删除 0：正常 1：停用
	 */
	@Excel(name = "状态",  replace = {"作废_-1", "启用_0", "停用_1"})
	@NotNull(message = "请选择状态", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "状态")
	private int status;

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

	/**
	 * 创建人
	 */
	@Excel(name = "创建人", width = 20)
	@ApiModelProperty(value = "创建人")
	private String creator;

}
