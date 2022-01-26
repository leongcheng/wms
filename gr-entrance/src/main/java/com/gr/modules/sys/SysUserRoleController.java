package com.gr.modules.sys;

import com.gr.constant.R;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysUserRoleEntity;
import com.gr.sys.service.SysUserRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 用户角色对照表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@RestController
@RequestMapping("sys/userrole")
public class SysUserRoleController
{
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:userrole:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysUserRoleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:userrole:info")
    public R info(@PathVariable("id") Long id){
			SysUserRoleEntity sysUserRole = sysUserRoleService.getById(id);

        return R.ok().put("sysUserRole", sysUserRole);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:userrole:save")
    public R save(@RequestBody SysUserRoleEntity sysUserRole){
		boolean result = sysUserRoleService.save(sysUserRole);
        if (result) {
			return R.ok();
		}
		return R.error();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:userrole:update")
    public R update(@RequestBody SysUserRoleEntity sysUserRole){
		boolean result = sysUserRoleService.updateById(sysUserRole);
        if (result) {
			return R.ok();
		}
		return R.error();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:userrole:delete")
    public R delete(@RequestBody Long[] ids){
		boolean result = sysUserRoleService.removeByIds(Arrays.asList(ids));
        if (result) {
			return R.ok();
		}
		return R.error();
    }

}
