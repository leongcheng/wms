package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysFeedbackEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 意见反馈表
 *
 * @author liangc
 * @date 2019-09-09 10:15:23
 */
@Mapper
public interface SysFeedbackDao extends BaseMapper<SysFeedbackEntity> {

}
