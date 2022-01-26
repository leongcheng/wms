package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysDictEntity;

import java.util.Map;

/**
 * 系统数据字典
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysDictService extends IService<SysDictEntity> {

	PageUtils queryPage(Map<String, Object> params);

}

