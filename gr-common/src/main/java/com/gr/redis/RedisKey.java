package com.gr.redis;

/**
 * Redis所有Key
 */
public class RedisKey {

    public static String getSysConfigKey(String key) { return "sys:config:" + key; }

    public static String getSessionKey(String key) { return "shiro:session:" + key; }

    public static String getLoginKey(String key) { return "sys:loginKey:" + key; }

    public static String getLoginLock(String key) { return "sys:loginlock:" + key; }

    public static String getDictConfigKey(String key) {
        return "sys:dict:" + key;
    }

}
