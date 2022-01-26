package com.gr.sys.entity;

import lombok.Data;

/**
 * 登录信息
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Data
public class SysLoginUserEntity {

    private String account;

    private String password;

    private Integer loginType;

    private boolean captchaOnOff;

    private String code;

    private String uuid;

    private boolean rememberMe;
}
