package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysLogDao extends BaseMapper<SysLogEntity> {

}
