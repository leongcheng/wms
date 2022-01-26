package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysFileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件上传记录表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysFileDao extends BaseMapper<SysFileEntity> {

}
