package com.gr.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优雅关机
 * @author liangc
 * @date 2022/1/14 17:26
 */
@RestController
public class ShutDownController implements ApplicationContextAware {
    private ApplicationContext context;

    @PostMapping("/shutdown")
    public String shutdown() {
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
        ctx.close();

        return "context is shutdown";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
