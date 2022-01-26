package com.gr.modules.monitor.controller;

import com.gr.constant.Constants;
import com.gr.constant.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;

/**
 *
 * 缓存监控
 * @author liangc
 * @date 2021/01/19 11:14:32
 *
 */
@Slf4j
@RestController
public class CacheController
{
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @RequestMapping("/monitor/cache")
    public R getInfo() throws Exception {

        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);

        LocalDate now = LocalDate.now();
        // 访问流量-查询所有Redis键
        Collection<String> keys = redisTemplate.keys("page:view:" + now.minusDays(1) + ":" + "*");

        List<Map<String, String>> mapList = new ArrayList<>();
        for (String key : keys)
        {
            Map<String, String> map = new HashMap<>();
            map.put("date", key.substring(21));
            map.put("yesterday_num", redisTemplate.opsForValue().get(key));

            String value = redisTemplate.opsForValue().get(Constants.PAGE_VIEW + now + ":" + map.get("date"));
            map.put("today_num", value);

            mapList.add(map);
        }
        //升序排序
        Collections.sort(mapList, Comparator.comparing(o -> o.get("date")));

        result.put("signUser", mapList);

        return R.ok().put("cacheList", result);
    }


    @RequestMapping("/monitor/clearAll")
    public R clearCache() {

        try {
            // 获取所有key
            Set<String> keys = redisTemplate.keys("*");
            assert keys != null;
            // 迭代
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                log.info("清理缓存： {} => {}", it.hasNext());
                // 循环删除
                redisTemplate.delete(it.next());
            }
        } catch (Exception e) {
            log.error("清理全局缓存失败{}", e.getMessage());
        }
        return R.ok();
    }

}
