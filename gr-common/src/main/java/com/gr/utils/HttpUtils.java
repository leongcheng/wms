package com.gr.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * http请求
 * @author: liangc
 * @date: 2021/7/23 16:52
 */
@Slf4j
@Component
public class HttpUtils {

    /**
     * 发送get请求
     * @param url    url
     * @param params  发送的参数
     * @return
     */
    public static JSONObject sendGetRequest(String url, Map<String, String> params){
        RestTemplate restTemplate = new RestTemplate();

        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        String result = restTemplate.getForObject(url, String.class, params);

        log.info("请求url地址{} 请求结果{}", params, result);

        return JSONObject.parseObject(result);
    }

    /**
     * 发送post请求
     * @param url      url
     * @param params  发送的参数
     * @return
     */
    public static JSONObject sendPostRequest(String url, Object params){
        RestTemplate restTemplate = new RestTemplate();

        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        String result = restTemplate.postForObject(url, params, String.class);

        log.info("请求url地址{} 请求结果{}", params, result);

        return JSONObject.parseObject(result);
    }

    /**
     * 发送post请求
     * @param url      url
     * @param params  发送的参数
     * @return
     */
    public static JSONObject sendPostRequest(String url, HttpHeaders headers, Map<String, Object> params){
        RestTemplate restTemplate = new RestTemplate();

        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.setContentType(MediaType.MULTIPART_FORM_DATA); //图片格式

        //将请求头部和参数合成一个请求
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        String result = restTemplate.postForObject(url, requestEntity, String.class);

        log.info("请求url地址{} 请求结果{}", params, result);

        return JSONObject.parseObject(result);
    }

    /**
     * 以表单形式发送post请求
     * @param url      url
     * @param params  发送的参数
     * @return
     */
    public static String sendPostRequest(String url, MultiValueMap<String, Object> params){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        //以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        log.info("请求url{} 请求结果{}", params, result);

        return result.getBody();
    }

}
