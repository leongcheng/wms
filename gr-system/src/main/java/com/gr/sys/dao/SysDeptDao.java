package com.gr.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysDeptEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 */
@Mapper
public interface SysDeptDao extends BaseMapper<SysDeptEntity> {

    /**
     * 查询公司列表
     * @param params
     * @return
     */
    List<SysDeptEntity> queryList(Map<String, Object> params);

    /**
     * 查询子公司ID列表
     *
     * @param parentId 上级公司ID
     */
    List<Long> queryDetpIdList(Long parentId);

    /**
     * 查询用户Id库区列表
     * @param userId
     * @return
     */
    List<Long> queryDeptCodeList(Long userId);
}
