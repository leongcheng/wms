package com.gr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.ip.AddressUtil;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysLogDao;
import com.gr.sys.entity.SysLogEntity;
import com.gr.sys.service.SysLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    @Autowired
    private AddressUtil addressUtil;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String information = (String) params.get("information");
        String username = (String) params.get("username");
        String status = (String) params.get("status");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");

        LambdaQueryWrapper<SysLogEntity> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(SysLogEntity::getStatus, status);
        }

        // 起始时间
        wrapper.apply(StringUtils.isNotBlank(startDate),"DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
                " >= DATE_FORMAT( '" + startDate + "', '%Y-%m-%d' )");
        // 截止时间
        wrapper.apply(StringUtils.isNotBlank(endDate),"DATE_FORMAT( create_time, '%Y-%m-%d' ) " +
                " <= DATE_FORMAT( '" + endDate + "', '%Y-%m-%d' )");

        if (StringUtils.isNotBlank(username)) {
            wrapper.and(Wrapper -> Wrapper.like(SysLogEntity::getUsername, username));
        }

        if (!StringUtils.isBlank(information)) {
            wrapper.and(Wrapper -> Wrapper
                    .like(SysLogEntity::getUsername, information).or()
                    .like(SysLogEntity::getAccount, information).or()
                    .like(SysLogEntity::getOperation, information).or()
                    .like(SysLogEntity::getLocation, information).or()
                    .like(SysLogEntity::getIpaddr, information));
        }
        wrapper.orderByDesc(SysLogEntity::getCreateTime);

        IPage<SysLogEntity> page = this.page(new Query<SysLogEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void saveLog(SysLogEntity sysLog) {
        //远程查询区域信息
        sysLog.setLocation(addressUtil.getAddressResolution(sysLog.getIpaddr()));

        this.save(sysLog);
    }

}
