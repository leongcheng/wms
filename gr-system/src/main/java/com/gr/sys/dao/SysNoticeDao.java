package com.gr.sys.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gr.sys.entity.SysNoticeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 站内消息
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Mapper
public interface SysNoticeDao extends BaseMapper<SysNoticeEntity> {

    public List<SysNoticeEntity> noticeTopSixList();

}
