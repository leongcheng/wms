package com.gr.exception;

import lombok.Getter;


@Getter
public enum ResultEnum {

   ERR001(500, "用户已存在"),

   ERR002(500, "请选择权限信息"),

   ERR003(500, "用户类型不能空，且不允许选择多个用户"),

   ERR004(500, "系统管理员不能删除"),

   ERR005(500, "当前用户不能删除"),

   ERR006(403, "用户信息已过期，请重新登录"),

   ERR007(403, "账号信息不存在"),

   ERR008(500, "账号已被锁定，请联系管理员"),

   ERR009(500, "验证码已过期"),

   ERR010(500, "验证码不正确"),

   ERR011(500, "多次验证不通过，请60秒后再试"),

   ERR012(500, "账号或密码不正确"),

   ERR013(401, "登录状态已过期，您可以继续留在该页面，或者重新登录"),

   ERR014(500, "手机号格式错误"),

   ERR015(500, "未检测到可用数据"),

   ERR016(500, "不能将自己踢出下线"),



   ERR021(500, "文件格式错误"),
   ;
   private int code;
   private String message;

   ResultEnum(Integer code, String message) {
      this.code = code;
      this.message = message;
   }
}
