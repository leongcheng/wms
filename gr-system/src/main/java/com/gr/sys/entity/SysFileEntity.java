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
 * 文件上传记录表
 *
 * @author liangc
 * @date 2022-01-22 11:22:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_file")
@ApiModel(value="sys_file对象", description="文件管理")
public class SysFileEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	@ApiModelProperty(value = "主键ID")
	private Long id;

	/**
	 * 唯一识别ID
	 */
	@Excel(name = "唯一识别ID", width = 20)
	@ApiModelProperty(value = "唯一识别ID")
	private String fileId;

	/**
	 * 文件名称
	 */
	@Excel(name = "文件名称", width = 20)
	@ApiModelProperty(value = "文件名称")
	private String name;

	/**
	 * 文件链接
	 */
	@Excel(name = "文件链接", width = 20)
	@ApiModelProperty(value = "文件链接")
	private String url;

	/**
	 * 文件类型
	 */
	@Excel(name = "文件类型", width = 20)
	@ApiModelProperty(value = "文件类型")
	private Integer fileType;

	/**
	 * 备注信息
	 */
	@Excel(name = "备注信息", width = 20)
	@ApiModelProperty(value = "备注信息")
	private String remark;

	/**
	 * 创建者
	 */
	@Excel(name = "创建者", width = 20)
	@ApiModelProperty(value = "创建者")
	private String creater;

	/**
	 * 创建时间
	 */
	@Excel(name = "创建时间", width = 20)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@Excel(name = "更新时间", width = 20)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ApiModelProperty(value = "更新时间")
	private LocalDateTime updateTime;

}
