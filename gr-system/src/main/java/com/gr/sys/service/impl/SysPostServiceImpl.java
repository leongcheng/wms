package com.gr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Constants;
import com.gr.constant.Query;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysPostDao;
import com.gr.sys.entity.SysPostEntity;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.service.SysPostService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysPostService")
public class SysPostServiceImpl extends ServiceImpl<SysPostDao, SysPostEntity> implements SysPostService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String information = (String) params.get("information");

        SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();

        IPage<SysPostEntity> page = this.page(
                new Query<SysPostEntity>().getPage(params),
                new LambdaQueryWrapper<SysPostEntity>()
                        .like(StringUtils.isNotBlank(information), SysPostEntity::getPostName, information).or()
                        .like(StringUtils.isNotBlank(information), SysPostEntity::getPostCode, information)
                        .ne(user.getUserId() != Constants.SUPER_ADMIN, SysPostEntity::getPostId, Constants.SUPER_ADMIN)
        );

        return new PageUtils(page);
    }
}
