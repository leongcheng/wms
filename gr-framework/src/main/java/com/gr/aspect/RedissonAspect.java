package com.gr.aspect;

import com.gr.annotation.LockModel;
import com.gr.annotation.RedissonLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redisson分布式锁
 * @author liangc
 * @date 2020/10/19 11:48
 */
@Slf4j
@Aspect
@Component
public class RedissonAspect {

    @Autowired(required = false)
    private RedissonClient redissonClient;

    /**
     * 切面环绕通知
     *
     * @param joinPoint
     * @param redissonLock
     * @return Object
     */
    @SneakyThrows
    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) {
        Object obj = null;
        long startTime = System.currentTimeMillis();
        log.info("进入RedisLock环绕通知....");
        RLock rLock = getLock(joinPoint, redissonLock);
        boolean res = false;
        //获取超时时间
        long expireSeconds = redissonLock.expireSeconds();
        //等待多久,n秒内获取不到锁，则直接返回
        long waitTime = redissonLock.waitTime();
        //执行aop
        if (rLock != null) {
            try {
                if (waitTime == -1) {
                    res = true;
                    //一直等待加锁
                    rLock.lock(expireSeconds, TimeUnit.MILLISECONDS);
                } else {
                    res = rLock.tryLock(waitTime, expireSeconds, TimeUnit.MILLISECONDS);
                }
                if (res) {
                    obj = joinPoint.proceed();
                } else {
                    log.error("获取锁异常");
                }
            } finally {
                if (res) {
                    rLock.unlock();
                }
            }
        }
        log.info("结束RedisLock环绕通知，总共耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
        return obj;
    }


    @SneakyThrows
    private RLock getLock(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) {
        String[] keys = redissonLock.lockKey();
        if (keys.length == 0) {
            throw new RuntimeException("keys不能为空");
        }
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        Object[] args = joinPoint.getArgs();

        LockModel lockModel = redissonLock.lockModel();
        if (!lockModel.equals(LockModel.MULTIPLE) && !lockModel.equals(LockModel.REDLOCK) && keys.length > 1) {
            throw new RuntimeException("参数有多个,锁模式为->" + lockModel.name() + ".无法锁定");
        }
        RLock rLock = null;
        String keyConstant = redissonLock.keyConstant();
        if (lockModel.equals(LockModel.AUTO)) {
            if (keys.length > 1) {
                lockModel = LockModel.REDLOCK;
            } else {
                lockModel = LockModel.REENTRANT;
            }
        }
        switch (lockModel) {
            case FAIR:
                rLock = redissonClient.getFairLock(getValueBySpEL(keys[0], parameterNames, args, keyConstant).get(0));
                break;
            case REDLOCK:
                List<RLock> rLocks = new ArrayList<>();
                for (String key : keys) {
                    List<String> valueBySpEL = getValueBySpEL(key, parameterNames, args, keyConstant);
                    for (String s : valueBySpEL) {
                        rLocks.add(redissonClient.getLock(s));
                    }
                }
                RLock[] locks = new RLock[rLocks.size()];
                int index = 0;
                for (RLock r : rLocks) {
                    locks[index++] = r;
                }
                rLock = new RedissonRedLock(locks);
                break;
            case MULTIPLE:
                rLocks = new ArrayList<>();

                for (String key : keys) {
                    List<String> valueBySpEL = getValueBySpEL(key, parameterNames, args, keyConstant);
                    for (String s : valueBySpEL) {
                        rLocks.add(redissonClient.getLock(s));
                    }
                }
                locks = new RLock[rLocks.size()];
                index = 0;
                for (RLock r : rLocks) {
                    locks[index++] = r;
                }
                rLock = new RedissonMultiLock(locks);
                break;
            case REENTRANT:
                List<String> valueBySpEL = getValueBySpEL(keys[0], parameterNames, args, keyConstant);
                //如果spel表达式是数组或者LIST 则使用红锁
                if (valueBySpEL.size() == 1) {
                    rLock = redissonClient.getLock(valueBySpEL.get(0));
                } else {
                    locks = new RLock[valueBySpEL.size()];
                    index = 0;
                    for (String s : valueBySpEL) {
                        locks[index++] = redissonClient.getLock(s);
                    }
                    rLock = new RedissonRedLock(locks);
                }
                break;
            case READ:
                rLock = redissonClient.getReadWriteLock(getValueBySpEL(keys[0], parameterNames, args, keyConstant).get(0)).readLock();
                break;
            case WRITE:
                rLock = redissonClient.getReadWriteLock(getValueBySpEL(keys[0], parameterNames, args, keyConstant).get(0)).writeLock();
                break;
        }
        return rLock;
    }


    /**
     * 通过spring SpEL 获取参数
     *
     * @param key            定义的key值 以#开头 例如:#user
     * @param parameterNames 形参
     * @param values         形参值
     * @param keyConstant    key的常亮
     * @return
     */
    public List<String> getValueBySpEL(String key, String[] parameterNames, Object[] values, String keyConstant) {
        List<String> keys = new ArrayList<>();
        if (!key.contains("#")) {
            String s = "redis:lock:" + key + keyConstant;
            log.info("lockKey:" + s);
            keys.add(s);
            return keys;
        }
        //spel解析器
        ExpressionParser parser = new SpelExpressionParser();
        //spel上下文
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], values[i]);
        }
        Expression expression = parser.parseExpression(key);
        Object value = expression.getValue(context);
        if (value != null) {
            if (value instanceof List) {
                List value1 = (List) value;
                for (Object o : value1) {
                    addKeys(keys, o, keyConstant);
                }
            } else if (value.getClass().isArray()) {
                Object[] obj = (Object[]) value;
                for (Object o : obj) {
                    addKeys(keys, o, keyConstant);
                }
            } else {
                addKeys(keys, value, keyConstant);
            }
        }
        log.info("表达式key={},value={}", key, keys);
        return keys;
    }

    private void addKeys(List<String> keys, Object o, String keyConstant) {
        keys.add("redis:lock:" + o.toString() + keyConstant);
    }
}
