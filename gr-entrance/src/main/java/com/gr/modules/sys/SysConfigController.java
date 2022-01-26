package com.gr.modules.sys;

import com.gr.constant.R;
import com.gr.utils.PageUtils;
import com.gr.validator.ValidatorUtils;
import com.gr.annotation.SysLog;
import com.gr.sys.entity.SysConfigEntity;
import com.gr.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * 系统配置
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Api(tags = "系统配置")
@RestController
@RequestMapping("sys/config")
public class SysConfigController
{
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 所有配置列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:config:list")
    @ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysConfigService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 配置信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("sys:config:info")
    @ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
    public R info(@PathVariable("id") Long id) {
        SysConfigEntity config = sysConfigService.getById(id);

        return R.ok().put("sysConfig", config);
    }

    /**
     * 保存配置
     */
    @SysLog("保存配置")
    @PostMapping("/save")
    @RequiresPermissions("sys:config:save")
    @ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
    public R save(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        config.setCreateTime(LocalDateTime.now());
        sysConfigService.saveConfig(config);

        return R.ok();
    }

    /**
     * 修改配置
     */
    @SysLog("修改配置")
    @PostMapping("/update")
    @RequiresPermissions("sys:config:update")
    @ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
    public R update(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        sysConfigService.update(config);

        return R.ok();
    }

    /**
     * 删除配置
     */
    @SysLog("删除配置")
    @PostMapping("/delete")
    @RequiresPermissions("sys:config:delete")
    @ApiOperation(value = "删除", notes = "主键ids")
    public R delete(@RequestBody Long[] ids) {
        sysConfigService.deleteBatch(ids);

        return R.ok();
    }

}
