package com.gr.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.gr.jwt.JwtFilter;
import com.gr.shiro.UserRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisClusterManager;
import org.crazycake.shiro.RedisManager;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Shiro的配置文件
 *
 */
@Slf4j
@Configuration
public class ShiroConfig {

	@Resource
	private LettuceConnectionFactory lettuceConnectionFactory;

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

		factoryBean.setSecurityManager(securityManager);
		factoryBean.setLoginUrl("/login"); // 定义登录的url
		factoryBean.setSuccessUrl("/console"); // 定义登录成功后的url
		factoryBean.setUnauthorizedUrl("/error/403.html"); // 没有权限时跳转的url

		Map<String, String> filterMap = new LinkedHashMap<>();
		// 放行不需要权限认证的接口
		filterMap.put("/static/**", "anon");
		filterMap.put("/favicon.ico", "anon");

		filterMap.put("/sys/captchaImage", "anon");
		filterMap.put("/sys/login", "anon");
		filterMap.put("/login", "anon");
		filterMap.put("/logout", "anon");
		filterMap.put("/error/**", "anon");
		filterMap.put("/pear.config.yml", "anon");
		filterMap.put("/", "anon");

		// swagger-ui
		filterMap.put("/swagger-ui.html", "anon");
		filterMap.put("/swagger**/**", "anon");
		// Knife4j API文档
		filterMap.put("/doc.html","anon");
		filterMap.put("/v2/api-docs", "anon");
		filterMap.put("/swagger-resources/**", "anon");
		filterMap.put("/webjars/**", "anon");

		// 添加自己的过滤器并且取名为jwt
		Map<String, Filter> filterRuleMap = new LinkedHashMap<>();
		// 设置我们自定义的JWT过滤器
		filterRuleMap.put("jwt", new JwtFilter());
		factoryBean.setFilters(filterRuleMap);
		// 所有请求通过我们自己的JWT Filter
		filterMap.put("/**", "jwt");

		factoryBean.setFilterChainDefinitionMap(filterMap);
		return factoryBean;

	}

	/**
	 * 安全管理器
	 */
	@Bean("securityManager")
	public DefaultWebSecurityManager securityManager(UserRealm authRealm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置自定义realm.
		securityManager.setRealm(authRealm);
		//禁用session, 不保存用户登录状态。保证每次请求都重新认证。
		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		securityManager.setSubjectDAO(subjectDAO);
		securityManager.setCacheManager(redisCacheManager());

		return securityManager;
	}

	/**
	 * cacheManager 缓存 redis实现
	 * 使用的是shiro-redis开源插件
	 *
	 * @return
	 */
	public RedisCacheManager redisCacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		redisCacheManager.setRedisManager(redisManager());
		//redis中针对不同用户缓存(此处的id需要对应user实体中的id字段,用于唯一标识)
		redisCacheManager.setPrincipalIdFieldName("userId");
		//用户权限信息缓存时间
		redisCacheManager.setExpire(20000);
		return redisCacheManager;
	}

	/**
	 * 配置shiro redisManager
	 * 使用的是shiro-redis开源插件
	 *
	 * @return
	 */
	@Bean
	public IRedisManager redisManager() {
		IRedisManager manager;
		// redis 单机支持，在集群为空，或者集群无机器时候使用
		if (lettuceConnectionFactory.getClusterConfiguration() == null || lettuceConnectionFactory.getClusterConfiguration().getClusterNodes().isEmpty()) {
			RedisManager redisManager = new RedisManager();
			redisManager.setHost(lettuceConnectionFactory.getHostName());
			redisManager.setPort(lettuceConnectionFactory.getPort());
			if (!org.springframework.util.StringUtils.isEmpty(lettuceConnectionFactory.getPassword())) {
				redisManager.setPassword(lettuceConnectionFactory.getPassword());
			}
			manager = redisManager;
		}else{
			// redis 集群支持，优先使用集群配置
			RedisClusterManager redisManager = new RedisClusterManager();
			Set<HostAndPort> portSet = new HashSet<>();
			lettuceConnectionFactory.getClusterConfiguration().getClusterNodes().forEach(node -> portSet.add(new HostAndPort(node.getHost() , node.getPort())));
			JedisCluster jedisCluster = new JedisCluster(portSet);
			redisManager.setJedisCluster(jedisCluster);
			manager = redisManager;
		}
		return manager;
	}


	@Bean
	public Redisson redisson(){
		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + lettuceConnectionFactory.getHostName() + ":" + lettuceConnectionFactory.getPort()).setPassword(StringUtils.isNotBlank(lettuceConnectionFactory.getPassword())?lettuceConnectionFactory.getPassword():null);

		return (Redisson) Redisson.create(config);
	}

	/**
	 * shiro整合thymeleaf
	 * @return
	 */
	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
		defaultAdvisorAutoProxyCreator.setUsePrefix(true);//解决重复代理问题
		defaultAdvisorAutoProxyCreator.setAdvisorBeanNamePrefix("_no_advisor");//添加前缀判断 不匹配 任何Advisor
		return defaultAdvisorAutoProxyCreator;
	}

	/**
	 * 生命周期管理
	 * @return
	 */
	@Bean("lifecycleBeanPostProcessor")
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * shiro 注解开启授权标识
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

}
