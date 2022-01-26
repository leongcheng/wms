package com.gr.sys.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gr.validator.group.AddGroup;
import com.gr.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 用户管理
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_user")
@ApiModel(value="sys_user对象", description="用户管理")
public class SysUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	@TableId
	private Long userId;

	/**
	 * 账号
	 */
	@Excel(name = "账号", width = 20)
	@NotBlank(message = "账号不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "账号")
	private String account;

	/**
	 * 用户名
	 */
	@Excel(name = "用户名", width = 20)
	@NotBlank(message = "用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "用户名")
	private String username;

	/**
	 * 性别
	 */
	@Excel(name = "性别", width = 20)
	@ApiModelProperty(value = "性别")
	private String sex;

	/**
	 * 年龄
	 */
	@Excel(name = "年龄", width = 20)
	@ApiModelProperty(value = "年龄")
	private int age;

	/**
	 * 登录密码
	 */
	@NotBlank(message = "登录密码不能为空", groups = {AddGroup.class})
	@ApiModelProperty(value = "登录密码")
	private String password;

	/**
	 * 盐
	 */
	@ApiModelProperty(value = "盐")
	private String salt;

	/**
	 * 部门编号
	 */
	@NotNull(message = "请选择所属部门", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "部门编号")
	private Integer deptId;

	/**
	 * 部门名称
	 */
	@Excel(name = "部门名称", width = 20)
	//@NotBlank(message = "请选择所属部门", groups = {AddGroup.class, UpdateGroup.class})
	@ApiModelProperty(value = "部门名称")
	private String deptName;

	/**
	 * 岗位ID
	 */
	@ApiModelProperty(value = "岗位ID")
	private Long postId;

	/**
	 * 岗位名称
	 */
	@Excel(name = "岗位名称", width = 20)
	@ApiModelProperty(value = "岗位名称")
	private String postName;

	/**
	 * 权限信息
	 */
	@Excel(name = "权限信息", width = 20)
	@ApiModelProperty(value = "权限信息")
	private String roleName;

	/**
	 * 手机号
	 */
	@Excel(name = "手机号", width = 20)
	@ApiModelProperty(value = "手机号")
	private String phone;

	/**
	 * Email邮箱
	 */
	@Excel(name = "Email邮箱", width = 20)
	@ApiModelProperty(value = "Email邮箱")
	private String email;

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
	 * 用户类型
	 */
	@ApiModelProperty(value = "用户类型")
	private String userType;

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

	/**
	 * 版本号
	 */
	@Version
	private Integer version;

	/**
	 * 逻辑删除
	 */
	@TableLogic
	private Integer deleted;

	/**
	 * 角色ID列表
	 */
	@TableField(exist = false)
	private List<Long> roleIdList;

	/**
	 * 岗位ID列表
	 */
	@TableField(exist = false)
	private List<Long> postIdList;

	/**
	 * 用户会话ID
	 */
	@TableField(exist = false)
	private String sid;

	/**
	 * 请求token
	 */
	@TableField(exist = false)
	private String token;

	/**
	 * 登录IP
	 */
	@TableField(exist = false)
	private String ip;

	/**
	 *浏览器
	 */
	@TableField(exist = false)
	private String browser;

	/**
	 * 操作系统
	 */
	@TableField(exist = false)
	private String system;

	/**
	 *上次登陆地点
	 */
	@TableField(exist = false)
	private String loginLocation;

	/**
	 * 上次登陆时间
	 */
	@TableField(exist = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date loginTime;

	/**
	 * 最后访问时间
	 */
	@TableField(exist = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastAccessTime;

	/**
	 * 用户是否在线 0.离线 1.在线
	 */
	@TableField(exist = false)
	private Integer loginStatus;

	/**
	 * 移动平台(0) 电脑设备(1)
	 */
	@TableField(exist = false)
	private Integer mobile;
}
