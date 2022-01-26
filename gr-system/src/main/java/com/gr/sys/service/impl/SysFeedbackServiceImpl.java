package com.gr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Constants;
import com.gr.constant.Query;
import com.gr.sys.entity.SysUserEntity;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysFeedbackDao;
import com.gr.sys.entity.SysFeedbackEntity;
import com.gr.sys.service.SysFeedbackService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("opinionService")
public class SysFeedbackServiceImpl extends ServiceImpl<SysFeedbackDao, SysFeedbackEntity> implements SysFeedbackService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String information = (String) params.get("information");
        String status = (String) params.get("status");

        SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();

        QueryWrapper<SysFeedbackEntity> wrapper = new QueryWrapper<>();

        if(StringUtils.isNotBlank(status)){
            wrapper.eq("status", status);
        }

        if(StringUtils.isNotBlank(information)){
            wrapper.and(Wrapper -> Wrapper.like(StringUtils.isNotBlank(information),"phone", information).or()
                    .like(StringUtils.isNotBlank(information),"remark", information).or()
                    .like(StringUtils.isNotBlank(information),"title", information).or()
                    .like(StringUtils.isNotBlank(information),"creator", information).or()
                    .like(StringUtils.isNotBlank(information),"content", information).or()
                    .like(StringUtils.isNotBlank(information),"reply", information));
        }

        if(user.getUserId() != Constants.SUPER_ADMIN){
            wrapper.eq("user_id", user.getUserId());
        }

        wrapper.orderByDesc("create_time");

        IPage<SysFeedbackEntity> page = this.page(new Query<SysFeedbackEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

}
