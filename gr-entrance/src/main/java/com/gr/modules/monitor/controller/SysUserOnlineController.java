package com.gr.modules.monitor.controller;

import com.gr.constant.Constants;
import com.gr.constant.R;
import com.gr.exception.ResultEnum;
import com.gr.exception.ResultException;
import com.gr.ip.AddressUtil;
import com.gr.utils.DateUtils;
import com.gr.annotation.SysLog;
import com.gr.redis.RedisUtils;
import com.gr.modules.sys.AbstractController;
import com.gr.sys.entity.SysUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线用户
 * @author liangc
 * @date 2020/3/30 16:27
 */
@Slf4j
@RestController
@RequestMapping("/sys/online")
public class SysUserOnlineController extends AbstractController
{
    @Autowired
    private AddressUtil addressUtil;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取在线用户
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:online:list")
    public R monitor(@RequestParam Map<String, Object> params) {
        List<SysUserEntity> list = new ArrayList<>();
        // 查询所有Redis键
        Collection<String> keys = redisUtils.keys(Constants.PREFIX_ONLINE_USER + "*");
        int time = 60 * 2;
        for (String key : keys) {
            SysUserEntity user = redisUtils.getCacheObject(key);
            if (user != null) {
                if(StringUtils.isNotBlank(user.getIp())){
                    user.setLoginLocation(addressUtil.getAddressResolution(user.getIp()));
                }
                if(user.getLoginStatus() == Constants.SUPER_ADMIN){
                    user.setLoginStatus(DateUtils.getMinutes(user.getLastAccessTime()) > time ? Constants.ZERO : Constants.SUPER_ADMIN);
                }
                list.add(user);
            }
        }

        //按时间降序
        List<SysUserEntity> sortList = list.stream().sorted(Comparator.comparing(SysUserEntity::getLastAccessTime).reversed()).collect(Collectors.toList());

        return R.ok().put("page", getDataTable(sortList, params));
    }

    /**
     * 踢出用户
     * @param ids
     * @return
     */
    @SysLog("踢出用户")
    @PostMapping("/batchForceLogout")
    @RequiresPermissions("sys:online:out")
    public R monitorOut(@RequestBody String[] ids, HttpServletRequest request) {
        for(String userId: ids){
            SysUserEntity sysUser = redisUtils.getCacheObject(Constants.PREFIX_ONLINE_USER + userId);
            if(sysUser != null && StringUtils.equals(userId, String.valueOf(getLoginUser().getUserId()))){
                throw new ResultException(ResultEnum.ERR016);

            }else if(sysUser != null){
                log.info("用户名:  " + sysUser.getUsername() + ",踢出成功！ ");
                //清空用户登录Token缓存
                redisUtils.set(Constants.PREFIX_USER_TOKEN + sysUser.getToken(), sysUser.setLoginStatus(Constants.ZERO));
                //清空用户登录Shiro权限缓存
                redisUtils.del(Constants.PREFIX_USER_SHIRO_CACHE + sysUser.getUserId());
                //更新用户在线状态
                redisUtils.del(Constants.PREFIX_ONLINE_USER + sysUser.getUserId());
            }
        }
        return R.ok();
    }
}
