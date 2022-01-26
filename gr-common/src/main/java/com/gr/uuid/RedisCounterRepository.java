package com.gr.uuid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * redis生成唯一id
 * @author liangc
 * @date 2020/8/18 9:34
 */
@Slf4j
@Repository
public class RedisCounterRepository {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 根据获取的自增数据,添加日期标识构造分布式全局唯一标识

    /**
     * 序列号长度：yyyyMMdd + 6位递增字符
     * @param changeNumPrefix 自增的redis原子
     * @return
     */
    public String getNumFromRedis(String changeNumPrefix) {
        return getNumFromRedis(changeNumPrefix,6);
    }

    /**
     * 序列号长度：yyyyMMdd + count位递增字符
     * @param changeNumPrefix  自增的redis原子
     * @param count            自定义序列号长度
     * @return
     */
    public String getNumFromRedis(String changeNumPrefix, int count) {
        String dateStr = LocalDate.now().format(dateTimeFormatter);
        Long value = incrementNum(changeNumPrefix + dateStr);
        String formatStr = "%0"+count+"d";
        return dateStr + getRandom(4) + String.format(formatStr,value);
    }

    /**
     * 从redis中获取自增数据(redis保证自增是原子操作)
     * @param key  redis自增的key值
     * @return
     */
    private long incrementNum(String key) {
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        if (null == factory) {
            log.error("Unable to connect to redis.");
        }
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, factory);
        long increment = redisAtomicLong.incrementAndGet();
        if (1 == increment) {
            // 如果数据是初次设置,需要设置超时时间
            redisAtomicLong.expire(1, TimeUnit.DAYS);
        }
        return increment;
    }


    /**
     * 生成指定位数的随机数字
     * @param len
     * @return
     */
    public static String generateCode(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0,len);
    }

    /**
     * 使用 ThreadLocalRandom 生成指定位数的随机数字
     * @param len
     * @return
     */
    public static String getRandom(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = ThreadLocalRandom.current().nextInt(Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0, len);
    }
}
