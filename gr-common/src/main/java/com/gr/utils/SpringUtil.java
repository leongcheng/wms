package com.gr.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * 
 * @author liangc
 * @date 2020-03-12 14:34
 */
@Component
public class SpringUtil implements BeanFactoryPostProcessor {

	private static ConfigurableListableBeanFactory BEAN_FACTORY;
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		SpringUtil.BEAN_FACTORY = beanFactory;
	}

	/**
	 * 获取对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) throws BeansException {
	    return (T) BEAN_FACTORY.getBean(name);
	}
	
	/**
	 * 获取类型为requiredType的对象
	 */
	public static <T> T getBean(Class<T> clz) throws BeansException {
	    T result = (T) BEAN_FACTORY.getBean(clz);
	    return result;
	}

	/**
	 * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
	 */
	public static boolean containsBean(String name)
	{
	    return BEAN_FACTORY.containsBean(name);
	}
	
	/**
	 * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 
	 * </P>
	 * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
	 */
	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
	    return BEAN_FACTORY.isSingleton(name);
	}
	

	public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
	    return BEAN_FACTORY.getType(name);
	}
	
	/**
	 * 如果给定的bean名字在bean定义中有别名，则返回这些别名
	 */
	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
	    return BEAN_FACTORY.getAliases(name);
	}

	/**
	 * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
	 *
	 * @param str 指定字符串
	 * @param strs 需要检查的字符串数组
	 * @return 是否匹配
	 */
	public static boolean matches(String str, List<String> strs)
	{
		if (StringUtils.isBlank(str) || strs == null || strs.size() == 0)
		{
			return false;
		}
		for (String pattern : strs)
		{
			if (isMatch(pattern, str))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断url是否与规则配置:
	 * ? 表示单个字符;
	 * * 表示一层路径内的任意字符串，不可跨层级;
	 * ** 表示任意层路径;
	 *
	 * @param pattern 匹配规则
	 * @param url 需要匹配的url
	 * @return
	 */
	public static boolean isMatch(String pattern, String url)
	{
		AntPathMatcher matcher = new AntPathMatcher();
		return matcher.match(pattern, url);
	}
}
