package com.gr.filter;


import com.gr.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 访问量统计
 *
 * @author liangc
 */
@Slf4j
@Component
public class SignUserFilter implements Filter
{
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        String key = Constants.PAGE_VIEW + LocalDate.now() + ":" + String.format("%0"+2+"d", LocalDateTime.now().getHour()) + ":00";

        stringRedisTemplate.opsForValue().increment(key, 1);
        stringRedisTemplate.expire(key,2, TimeUnit.DAYS);
        //redisTemplate.opsForValue().set(key, incr(key, 1, 1), 3, TimeUnit.DAYS);

        log.info("redis 访问统计{}", Integer.parseInt(stringRedisTemplate.opsForValue().get(key)));

        //拦截放行继续下一步
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    /**
     * redis 自增
     * @param key
     * @param liveTime 毫秒数 这个计数器的有效存留时间
     * @param delta 自增量
     * @return
     */
    public Long incr(String key, long liveTime, long delta) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.addAndGet(delta);

        if ((null == increment || increment.longValue() == 0) && liveTime > 0) { //初始设置过期时间
            entityIdCounter.expire(liveTime, TimeUnit.DAYS);
        }

        return increment;
    }
}
