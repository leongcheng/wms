package com.gr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Constants;
import com.gr.constant.Query;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysLoginInforDao;
import com.gr.sys.entity.SysLoginInforEntity;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.service.SysLoginInforService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 登录日志
 */

@Service("sysLoginInforService")
public class SysLoginInforServiceImpl extends ServiceImpl<SysLoginInforDao, SysLoginInforEntity> implements SysLoginInforService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String information = (String) params.get("information");
        String username = (String) params.get("username");
        String status = (String) params.get("status");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");

        QueryWrapper<SysLoginInforEntity> wrapper = new QueryWrapper<>();

        if (!StringUtils.isBlank(status)) {
            wrapper.eq("status", status);
        }

        // 起始时间
        wrapper.apply(StringUtils.isNotBlank(startDate),"DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
                " >= DATE_FORMAT( '" + startDate + "', '%Y-%m-%d' )");
        // 截止时间
        wrapper.apply(StringUtils.isNotBlank(endDate),"DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
                " <= DATE_FORMAT( '" + endDate + "', '%Y-%m-%d' )");

        if (StringUtils.isNotBlank(username)) {
            wrapper.and(Wrapper -> Wrapper.like("username", username));
        }

        if (!StringUtils.isBlank(information)) {
            wrapper.and(Wrapper -> Wrapper.like("username", information).or()
                    .like("account", information).or()
                    .like("location", information).or()
                    .like("ipaddr", information));
        }
        SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();

        wrapper.eq(user.getUserId() != Constants.SUPER_ADMIN,"account", user.getUsername())
                .orderByDesc("create_time");

        IPage<SysLoginInforEntity> page = this.page(new Query<SysLoginInforEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }
}
