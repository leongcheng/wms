package com.gr.ip;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gr.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 远程查询地址
 * @author liangc
 * @date 2020/4/30 12:07
 */
@Slf4j
@Component
public class AddressUtil {

    @Resource
    private RedisTemplate redisTemplate;

    // 百度API查询 个人一天1000次
    private static final String IP_BAIDU_URL =  "http://api.map.baidu.com/location/ip?ak=gRhqOOqPOQzvM8nMRnVoQswejvggglqY&ip={}&coor=bd09ll";

    // 备用第二查询接口
    private static final String IP_WHOIS_URL =  "http://whois.pconline.com.cn/ipJson.jsp?ip={}&json=true";

    // 请求IP前缀
    private static final String ADDRESS_KEY = "ip:address:";

    // 内网地址
    private static final String ADDRESS = "内网IP";

    // 未知地址
    private static final String UNKNOWN = "XX XX";

    /**
     * 远程查询操作地点
     * @param ip
     * @return
     */
    public String getAddressResolution (String ip){
        //检查缓存中是否存在
        String key = ADDRESS_KEY + ip;
        if(redisTemplate.hasKey(key)) {
            return redisTemplate.opsForValue().get(key).toString();
        }
        //远程查询区域信息
        String address = getRealAddressByIP(ip);
        if(!address.equals(UNKNOWN)) {
            redisTemplate.opsForValue().set(key, address, 7, TimeUnit.DAYS); //redis缓存操作地点
        }

        return address;
    }

    private static String getRealAddressByIP(String ip) {
        String address = UNKNOWN;
        // 内网不查询
        if (NetUtil.isInnerIP(ip)){
            return ADDRESS;
        }
        JSONObject obj;
        try{
            HttpResponse body = HttpRequest.get(StrUtil.format(IP_BAIDU_URL, ip)).charset("GBK").execute();
            if (StrUtil.isBlank(body.body())){
                log.error("获取地理位置异常 {}", ip);
                return address;
            }
            obj = JSONUtil.parseObj(body.body());
            //{"address":"CN|上海|上海|None|CHINANET|0|0","content":{"address_detail":{"province":"上海市","city":"上海市","street":"","district":"","street_number":"","city_code":289},"address":"上海市","point":{"x":"121.48789949","y":"31.24916171"}},"status":0}
            int error_code = obj.getInt("status",-1);
            if(error_code == 0) {
                JSONObject data = obj.getJSONObject("content").getJSONObject("address_detail");
                String country = obj.getStr("address", "CN|上海|上海|None|CHINANET|0|0").split("\\|")[0];
                String province = data.getStr("province", "上海");
                String city = data.getStr("city", "上海市");
                address = StrUtil.format("{} {}-{}", country,province,city);
            }else {
                body = HttpRequest.get(StrUtil.format(IP_WHOIS_URL, ip)).charset(Constants.GBK).execute();
                if (StrUtil.isBlank(body.body())) {
                    log.error("获取地理位置异常 {}", ip);
                    return address;
                }
                obj = JSONUtil.parseObj(body.body());
                //{"ip":"58.210.19.94","pro":"江苏省","proCode":"320000","city":"苏州市","cityCode":"320500","region":"张家港市","regionCode":"320582","addr":"江苏省苏州市张家港市 电信ADSL","regionNames":"","err":""}
                if(StringUtils.isNotEmpty(obj.getStr("addr"))){
                    String pro = obj.getStr("pro");
                    String city = obj.getStr("city");
                    //String region = obj.getStr("region");
                    address = String.format("%s %s", pro,city);
                }
            }
        } catch (Exception e){
            log.error("获取地理位置异常 {}", ip);
        }
        return address;
    }

}
