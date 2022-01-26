package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysPostEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位信息表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysPostDao extends BaseMapper<SysPostEntity> {

}
