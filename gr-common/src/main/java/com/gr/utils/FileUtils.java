package com.gr.utils;

import lombok.Data;

/**
 * 上传文件
 * @author liangc
 * @date 2020/6/11 14:50
 */
@Data
public class FileUtils {

    private String fileId;

    /*文件名*/
    private String fileName;

    /*文件地址*/
    private String baseUrl;

}
