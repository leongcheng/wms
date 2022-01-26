package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysDictEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统数据字典
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysDictDao extends BaseMapper<SysDictEntity> {

	Long getMaxCodeByType(@Param(value = "type") String type);

	List<SysDictEntity> getAllDictTypeList();

}
