package com.gr.sys.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
import java.util.List;


/**
 * 部门管理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_dept")
@ApiModel(value="sys_dept对象", description="部门管理")
public class SysDeptEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private Long deptId;

    /**
     * 上级部门ID，一级部门为0
     */
    @ApiModelProperty(value = "上级部门ID，一级部门为0")
    private Long parentId;

    /**
     * 上级部门名称
     */
    @ApiModelProperty(value = "上级部门名称")
    @TableField(exist = false)
    private String parentName;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称",  width = 20)
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    /**
     * 部门编号
     */
    @Excel(name = "部门编号",  width = 20)
    @ApiModelProperty(value = "部门编号")
    private String deptCode;

    /**
     * 负责人
     */
    @Excel(name = "负责人",  width = 20)
    @ApiModelProperty(value = "负责人")
    private String leader;

    /**
     * 联系方式
     */
    @Excel(name = "联系方式",  width = 20)
    @ApiModelProperty(value = "联系方式")
    private String phone;

    /**
     * 电子邮箱
     */
    @Excel(name = "电子邮箱",  width = 20)
    @ApiModelProperty(value = "电子邮箱")
    private String email;

    /**
     * 排序
     */
    @Excel(name = "排序",  width = 20)
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 是否删除  -1：已删除  0：正常
     */
    @ApiModelProperty(value = "是否删除  -1：已删除  0：正常")
    @TableLogic
    private Integer deleted;

    /**
     * 状态，-1：删除 0：正常 1：停用
     */
    @Excel(name = "状态",  replace = {"作废_-1", "启用_0", "停用_1"})
    @ApiModelProperty(value = "状态")
    private int status;

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

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<SysDeptEntity> children;
}
