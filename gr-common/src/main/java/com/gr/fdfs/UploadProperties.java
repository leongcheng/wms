package com.gr.fdfs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liangc
 * @date 2019/9/16
 */
@Data
@ConfigurationProperties(prefix = "fast.upload")
@Component
public class UploadProperties {

    /*地址前缀*/
    private String baseUrl;

    /*文件类型*/
    private List<String> allowTypes;
}
