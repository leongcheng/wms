package com.gr.aspect;

import com.google.gson.Gson;
import com.gr.constant.Constants;
import com.gr.exception.RRException;
import com.gr.ip.IPUtils;
import com.gr.annotation.SysLog;
import com.gr.redis.RedisKey;
import com.gr.sys.entity.SysLogEntity;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 系统日志，切面处理类
 *
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {

	@Autowired
	private SysLogService sysLogService;
	@Resource
	private RedisTemplate redisTemplate;

	private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

	@Pointcut("@annotation(com.gr.annotation.SysLog)")
	public void logPointCut() {

	}

	/**
	 * 处理请求之前执行
	 *
	 * @param joinPoint 切点
	 */
	@Before("logPointCut()")
	public void boBefore(JoinPoint joinPoint) {
		//执行时长记录
		startTime.set(System.currentTimeMillis());
		//系统维护检查
		if (redisTemplate.hasKey(RedisKey.getSysConfigKey("system:update"))) {
			log.info("**********请求被拦截**********");
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();

			SysLog controllerLog = method.getAnnotation(SysLog.class);
			//注解上的描述
			if (!controllerLog.value().contains("系统参数")) {
				throw new RRException(redisTemplate.opsForValue().get(RedisKey.getSysConfigKey("system:update")).toString());
			}
		}
	}

	/**
	 * 处理完请求后执行
	 *
	 * @param joinPoint 切点
	 */
	@AfterReturning(pointcut = "logPointCut()")
	public void doAfterReturning(JoinPoint joinPoint) {
		handleLog(joinPoint, null);
	}

	/**
	 * 拦截异常操作
	 *
	 * @param joinPoint 切点
	 * @param e         异常
	 */
	@AfterThrowing(value = "logPointCut()", throwing = "e")
	public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
		handleLog(joinPoint, e);
	}


	protected void handleLog(final JoinPoint joinPoint, final Exception e) {
		try {
			long time = System.currentTimeMillis() - startTime.get();
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();

			SysLogEntity sysLog = new SysLogEntity();
			SysLog controllerLog = method.getAnnotation(SysLog.class);
			if (controllerLog != null) {
				//注解上的描述
				sysLog.setOperation(controllerLog.value());
			}
			sysLog.setStatus(Constants.ZERO);
			//请求的方法名
			String className = joinPoint.getTarget().getClass().getName();
			String methodName = signature.getName();
			sysLog.setMethod(className + "." + methodName + "()");

			//请求的参数
			Object[] args = joinPoint.getArgs();
			String params = new Gson().toJson(args[0]);
			sysLog.setParams(params);
			if (e != null) {
				sysLog.setStatus(Constants.SUPER_ADMIN);
				sysLog.setMessage(StringUtils.substring(e.getMessage(), 0, 5000));
			}
			//登录用户
			SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
			sysLog.setUsername(user.getUsername());
			sysLog.setAccount(user.getAccount());
			sysLog.setIpaddr(IPUtils.getIpAddr());
			sysLog.setTime(time);
			sysLog.setCreateTime(LocalDateTime.now());

			//保存系统日志(异步)
			sysLogService.saveLog(sysLog);

		} catch (Exception ex) {
			// 记录本地异常日志
			log.error("保存系统日志失败{}", ex.getMessage());
		} finally {
			startTime.remove();
		}
	}
}
