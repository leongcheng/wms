package com.gr.modules.sys;

import com.gr.constant.R;
import com.gr.utils.PageUtils;
import com.gr.sys.service.SysUserPostService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 用户与岗位关联表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@RestController
@RequestMapping("sys/userpost")
public class SysUserPostController
{
    @Autowired
    private SysUserPostService sysUserPostService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:userpost:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysUserPostService.queryPage(params);

        return R.ok().put("page", page);
    }

}
