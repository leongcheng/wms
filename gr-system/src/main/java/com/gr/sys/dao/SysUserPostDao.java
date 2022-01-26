package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysUserPostEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户与岗位关联表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysUserPostDao extends BaseMapper<SysUserPostEntity> {

}
