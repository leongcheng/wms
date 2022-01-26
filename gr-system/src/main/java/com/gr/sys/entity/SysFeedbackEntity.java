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
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 意见反馈表
 *
 * @author liangc
 * @date 2019-09-09 10:15:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_feedback")
@ApiModel(value="sys_config对象", description="意见反馈")
public class SysFeedbackEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 意见反馈ID
     */
    @TableId
    private Long feedbackId;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID", width = 20)
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话", width = 20)
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @Excel(name = "邮箱", width = 20)
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 标题
     */
    @Excel(name = "标题", width = 20)
    @NotBlank(message = "标题不能为空", groups = {UpdateGroup.class})
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 内容
     */
    @Excel(name = "内容", width = 50)
    @NotBlank(message = "内容不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 回复
     */
    @Excel(name = "回复", width = 50)
    @ApiModelProperty(value = "回复")
    private String reply;

    /**
     * 类型
     */
    @Excel(name = "类型")
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 状态
     */
    @Excel(name = "状态", replace = {"启用_0","停用_1"})
    @ApiModelProperty(value = "状态 0.启用 1.停用")
    private Integer status;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 20)
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 20)
    @ApiModelProperty(value = "创建人")
    private String creator;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
