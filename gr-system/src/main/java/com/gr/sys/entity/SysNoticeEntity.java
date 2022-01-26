package com.gr.sys.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gr.validator.group.AddGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 站内消息
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_notice")
public class SysNoticeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    private Long noticeId;
    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空", groups = {AddGroup.class})
    private String title;
    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空", groups = {AddGroup.class})
    private String content;
    /**
     * 链接
     */
    private String fileId;
    /**
     * 公告类型
     */
    @NotNull(message = "公告类型不能为空", groups = {AddGroup.class})
    private Integer type;
    /**
     * 状态 0.启用 1.停用
     */
    private Integer status;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 公告时间
     */
    private LocalDate date;
    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 改修日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    /**
     * 注备
     */
    private String remark;

    @TableField(exist = false)
    private List<Map<String, Object>> fileList;
}
