package com.gr.modules.sys;

import com.gr.constant.R;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysRoleMenuEntity;
import com.gr.sys.service.SysRoleMenuService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 角色菜单表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@RestController
@RequestMapping("sys/rolemenu")
public class SysRoleMenuController
{
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:rolemenu:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysRoleMenuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:rolemenu:info")
    public R info(@PathVariable("id") Long id){
			SysRoleMenuEntity sysRoleMenu = sysRoleMenuService.getById(id);

        return R.ok().put("sysRoleMenu", sysRoleMenu);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:rolemenu:save")
    public R save(@RequestBody SysRoleMenuEntity sysRoleMenu){
		boolean result = sysRoleMenuService.save(sysRoleMenu);
        if (result) {
			return R.ok();
		}
		return R.error();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:rolemenu:update")
    public R update(@RequestBody SysRoleMenuEntity sysRoleMenu){
		boolean result = sysRoleMenuService.updateById(sysRoleMenu);
        if (result) {
			return R.ok();
		}
		return R.error();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:rolemenu:delete")
    public R delete(@RequestBody Long[] ids){
		boolean result = sysRoleMenuService.removeByIds(Arrays.asList(ids));
        if (result) {
			return R.ok();
		}
		return R.error();
    }

}
