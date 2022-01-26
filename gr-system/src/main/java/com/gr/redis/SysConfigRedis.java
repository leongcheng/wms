package com.gr.redis;


import com.gr.sys.entity.SysConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统配置Redis
 */
@Component
public class SysConfigRedis
{
    @Autowired
    private RedisUtils redisUtils;

    public void saveOrUpdate(SysConfigEntity config) {
        if (config == null) {
            return;
        }
        String key = RedisKey.getSysConfigKey(config.getParamKey());
        redisUtils.set(key, config.getParamValue());
    }

    public void delete(String configKey) {
        String key = RedisKey.getSysConfigKey(configKey);
        redisUtils.del(key);
    }

    public SysConfigEntity get(String configKey) {
        String key = RedisKey.getSysConfigKey(configKey);
        return redisUtils.getCacheObject(key);
    }
}
