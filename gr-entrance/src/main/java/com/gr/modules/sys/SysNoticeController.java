package com.gr.modules.sys;

import com.gr.constant.R;
import com.gr.utils.PageUtils;
import com.gr.validator.ValidatorUtils;
import com.gr.validator.group.AddGroup;
import com.gr.validator.group.UpdateGroup;
import com.gr.annotation.SysLog;
import com.gr.sys.entity.SysNoticeEntity;
import com.gr.sys.service.SysNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;


/**
 * 站内消息
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Api(tags = "站内消息")
@RestController
@RequestMapping("sys/notice")
public class SysNoticeController extends AbstractController
{
    @Autowired
    private SysNoticeService sysNoticeService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysNoticeService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{noticeId}")
    @ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
    public R info(@PathVariable("noticeId") Long noticeId) {
        SysNoticeEntity sysNotice = sysNoticeService.getById(noticeId);

        return R.ok().put("sysNotice", sysNotice);
    }

    /**
     * 保存
     */
    @SysLog("新增公告")
    @PostMapping("/save")
    @RequiresPermissions("sys:notice:save")
    @ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
    public R save(@RequestBody SysNoticeEntity notice) {
        ValidatorUtils.validateEntity(notice, AddGroup.class);

        notice.setDate(LocalDate.now())
                .setCreator(getLoginUser().getUsername())
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(notice.getCreateTime());

        sysNoticeService.save(notice);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改公告")
    @PostMapping("/update")
    @RequiresPermissions("sys:notice:update")
    @ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
    public R update(@RequestBody SysNoticeEntity notice) {
        ValidatorUtils.validateEntity(notice, UpdateGroup.class);

        notice.setUpdateTime(LocalDateTime.now());

        sysNoticeService.updateById(notice);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除公告")
    @PostMapping("/delete")
    @RequiresPermissions("sys:notice:delete")
    @ApiOperation(value = "删除", notes = "@RequestBody接收ids")
    public R delete(@RequestBody Long[] noticeIds) {
        sysNoticeService.removeByIds(Arrays.asList(noticeIds));

        return R.ok();
    }

}
