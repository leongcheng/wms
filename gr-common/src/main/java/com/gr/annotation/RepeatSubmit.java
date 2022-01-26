package com.gr.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解防止表单重复提交
 * @author liangc
 * @date 2020/4/10 11:00
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
}
