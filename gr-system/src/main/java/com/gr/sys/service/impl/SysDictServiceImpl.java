package com.gr.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysDictDao;
import com.gr.sys.entity.SysDictEntity;
import com.gr.sys.service.SysDictService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysDictService")
public class SysDictServiceImpl extends ServiceImpl<SysDictDao, SysDictEntity> implements SysDictService {

	@Override
    public PageUtils queryPage(Map<String, Object> params) {

        IPage<SysDictEntity> page = this.page(
                new Query<SysDictEntity>().getPage(params),
                new LambdaQueryWrapper<SysDictEntity>()
                .orderByDesc(SysDictEntity::getCreateTime)
        );

        return new PageUtils(page);
    }

}
