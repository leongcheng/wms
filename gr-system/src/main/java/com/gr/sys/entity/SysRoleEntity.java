package com.gr.sys.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
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
import java.util.List;

/**
 * 角色管理
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_role")
@ApiModel(value="sys_role对象", description="角色管理")
public class SysRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 权限id
	 */
	@TableId
	private Long roleId;

	/**
	 * 角色名称
	 */
	@Excel(name = "角色名称", width = 20)
	@NotBlank(message = "名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "角色名称")
	private String roleName;

	/**
	 * 权限字符
	 */
	@Excel(name = "权限字符", width = 20)
	@NotBlank(message = "权限字符不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "权限字符")
	private String roleKey;

	/**
	 * 角色类型: 1.证书用户  0.普通用户
	 */
	@Excel(name = "角色类型", width = 20)
	//@NotNull(message = "角色类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "角色类型: 1.证书用户  0.普通用户")
	private Integer roleType;

	/**
	 * 角色排序
	 */
	@Excel(name = "排序", width = 20)
	@NotNull(message = "角色排序不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "角色排序")
	private int roleSort;

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


	@TableField(exist = false)
	private List<Long> menuIdList;
}
