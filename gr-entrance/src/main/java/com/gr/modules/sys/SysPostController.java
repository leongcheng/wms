package com.gr.modules.sys;

import com.gr.constant.R;
import com.gr.utils.PageUtils;
import com.gr.validator.ValidatorUtils;
import com.gr.validator.group.AddGroup;
import com.gr.validator.group.UpdateGroup;
import com.gr.annotation.RepeatSubmit;
import com.gr.annotation.SysLog;
import com.gr.sys.entity.SysPostEntity;
import com.gr.sys.service.SysPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;


/**
 * 岗位信息
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Api(tags = "岗位信息")
@RestController
@RequestMapping("sys/post")
public class SysPostController extends AbstractController
{
    @Autowired
    private SysPostService sysPostService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:post:list")
    @ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysPostService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{postId}")
    @RequiresPermissions("sys:post:info")
    @ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
    public R info(@PathVariable("postId") Long postId){
        SysPostEntity sysPost = sysPostService.getById(postId);

        return R.ok().put("sysPost", sysPost);
    }

    /**
     * 保存
     */
    @SysLog("新增岗位")
    @PostMapping("/save")
    @RequiresPermissions("sys:post:save")
    @ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
    @RepeatSubmit
    public R save(@RequestBody SysPostEntity sysPost){
        ValidatorUtils.validateEntity(sysPost, AddGroup.class);

        sysPost.setCreateTime(LocalDateTime.now()).setUpdateTime(sysPost.getCreateTime()).setCreator(getLoginUser().getUsername());

        sysPostService.save(sysPost);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改岗位")
    @PostMapping("/update")
    @RequiresPermissions("sys:post:update")
    @ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
    @RepeatSubmit
    public R update(@RequestBody SysPostEntity sysPost){
        ValidatorUtils.validateEntity(sysPost, UpdateGroup.class);

        sysPost.setUpdateTime(LocalDateTime.now()).setCreator(getLoginUser().getUsername());

        sysPostService.updateById(sysPost);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除岗位")
    @PostMapping("/delete")
    @RequiresPermissions("sys:post:delete")
    @ApiOperation(value = "删除", notes = "@RequestBody接收ids")
    @RepeatSubmit
    public R delete(@RequestBody Long[] postIds){
        sysPostService.removeByIds(Arrays.asList(postIds));

        return R.ok();
    }

}
