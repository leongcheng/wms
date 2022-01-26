package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限角色表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {

}
