package com.gr.quartz.task;

/**
 * 定时任务接口
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
public interface ITask {

    /**
     * 执行定时任务接口
     *
     * @param params   参数，多参数使用JSON数据
     */
    void run(String params);
}