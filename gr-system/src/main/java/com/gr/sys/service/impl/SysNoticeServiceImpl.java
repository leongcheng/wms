package com.gr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysNoticeDao;
import com.gr.sys.entity.SysNoticeEntity;
import com.gr.sys.service.SysNoticeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("NoticeService")
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeDao, SysNoticeEntity> implements SysNoticeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String information = (String) params.get("information");
        String date = (String) params.get("date");
        String type = (String) params.get("type");

        IPage<SysNoticeEntity> page = this.page(
                new Query<SysNoticeEntity>().getPage(params),
                new QueryWrapper<SysNoticeEntity>()
                        .like(StringUtils.isNotBlank(information),"title",information)
                        .eq(StringUtils.isNotBlank(type),"type", type)
                        .ne(StringUtils.isNotBlank(type),"status", 1)
                        .ge(StringUtils.isNotBlank(date), "date", date)
                        .orderByDesc("create_time")
        );

        return new PageUtils(page);
    }

}
