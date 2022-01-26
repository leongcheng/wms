package com.gr.constant;

/**
 * 常量
 *
 */
public class Constants
{
    /** 超级管理员ID */
    public static final int SUPER_ADMIN = 1;
    /** 状态 - 0 */
    public static final int ZERO = 0;
    /** 状态 - 2 */
    public static final int TWO = 2;
    /** 未知IP */
    public static final String IpAddr1 = "0:0:0:0:0:0:0:1";
    /** 本机IP */
    public static final String IpAddr2 = "127.0.0.1";
    /** 当前页码 */
    public static final String PAGE = "page";
    /** 每页显示记录数 */
    public static final String LIMIT = "limit";
    /** 排序字段 */
    public static final String ORDER_FIELD = "sidx";
    /** 排序方式 */
    public static final String ORDER = "order";
    /** 升序 */
    public static final String ASC = "asc";
    /** 数据权限过滤 */
    public static final String SQL_FILTER = "sql_filter";
    /**26个大写英文字母数组*/
    public static final String ENGLISH_GRAPHEME[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N", "O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    /** UTF-8 字符集*/
    public static final String UTF8 = "UTF-8";
    /** GBK 字符集*/
    public static final String GBK = "GBK";
    /** 登录用户Shiro权限缓存KEY前缀 */
    public static String PREFIX_USER_SHIRO_CACHE  = "shiro:cache:com.gr.shiro.UserRealm.authorizationCache:";
    /** 清除Shiro权限缓存KEY前缀 */
    public static final String PREFIX_SHIRO_CACHE = "shiro:cache:";
    /** 登录用户Token令牌缓存KEY前缀 */
    public static final String PREFIX_USER_TOKEN  = "heade:access_token:";
    /** 在线用户Token令牌缓存KEY前缀 */
    public static final String PREFIX_ONLINE_USER = "monitor:online_user:";
    /** 统计用户访问流量前缀 */
    public static String PAGE_VIEW = "page:view:";
    /** 微信用户sessionKey缓存KEY前缀 */
    public static final String WX_SESSION_ID = "wx:sessionKey:";
    /** 微信用户auth_token缓存KEY前缀 */
    public static final String WX_AUTH_TOKEN = "wx:auth_token:";
    /** token前缀auth_token */
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    /** 防重提交 redis key */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";
    /**
     * 菜单类型
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮功能
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
