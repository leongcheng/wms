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
 * 菜单表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_menu")
@ApiModel(value="sys_menu对象", description="菜单管理")
public class SysMenuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 菜单编号
	 */
	@TableId
	@ApiModelProperty(value = "主键ID")
	private Long menuId;

	/**
	 * 父节点
	 */
	@Excel(name = "父节点", width = 20)
	@NotNull(message = "父节点不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "父节点")
	private Long parentId;

	/**
	 * 标题
	 */
	@Excel(name = "标题", width = 20)
	@NotBlank(message = "标题不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "标题")
	private String title;

	/**
	 * 父菜单名称
	 */
	@TableField(exist=false)
	private String parentName;

	/**
	 * 菜单类型
	 */
	@Excel(name = "菜单类型", width = 20)
	@NotNull(message = "菜单类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "菜单类型")
	private Integer type;

	/**
	 * 打开类型
	 */
	@Excel(name = "打开类型", width = 20)
	@ApiModelProperty(value = "打开类型")
	private String openType;

	/**
	 * 图标
	 */
	@Excel(name = "图标", width = 20)
	@ApiModelProperty(value = "图标")
	private String icon;

	/**
	 * 跳转路径
	 */
	@Excel(name = "跳转路径", width = 20)
	@ApiModelProperty(value = "跳转路径")
	private String url;

	/**
	 * 参数
	 */
	@Excel(name = "参数", width = 20)
	@ApiModelProperty(value = "参数")
	private String params;

	/**
	 * 操作类型 移动平台(0) 电脑设备(1)
	 */
	@Excel(name = "操作类型", width = 20)
	@ApiModelProperty(value = "操作类型")
	private String pageType;

	/**
	 * 排序
	 */
	@Excel(name = "排序", width = 20)
	@ApiModelProperty(value = "排序")
	private int sort;

	/**
	 * 状态
	 */
	@Excel(name = "状态", width = 20)
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
	 * 子菜单
	 */
	@TableField(exist = false)
	private List<SysMenuEntity> children;

	@TableField(exist = false)
	public boolean open;
}
