package com.gr.modules.monitor.entity;

import lombok.Data;

/**
 * 系统相关信息
 *
 * @author liangc
 * @date 2020/04/19 11:15:54
 */
@Data
public class SystemEntity {
    /**
     * 服务器名称
     */
    private String computerName;

    /**
     * 服务器Ip
     */
    private String computerIp;

    /**
     * 项目路径
     */
    private String userDir;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 系统架构
     */
    private String osArch;

    /**
     * jco系统编号
     */
    private String client;

    /**
     * jco服务器
     */
    private String ashost;

}
