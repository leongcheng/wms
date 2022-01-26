package com.gr.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.sys.entity.SysDeptEntity;
import com.gr.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 */
public interface SysDeptService extends IService<SysDeptEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询公司列表
     * @param map
     * @return
     */
    List<SysDeptEntity> queryList(Map<String, Object> map);

    /**
     * 查询子公司ID列表
     *
     * @param parentId 上级公司ID
     */
    List<Long> queryDetpIdList(Long parentId);

    /**
     * 获取子公司ID，用于数据过滤
     */
    List<Long> getSubDeptIdList(Long deptId);

    /**
     * 获取公司信息
     *
     * @return
     */
    List<SysDeptEntity> getAllDeptList();

    /**
     * 查询部门树结构
     * @return
     */
    List<SysDeptEntity> buildMenuTreeSelect();
}
