package com.gr.modules.sys;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.gr.annotation.RepeatSubmit;
import com.gr.annotation.SysLog;
import com.gr.constant.Constants;
import com.gr.constant.R;
import com.gr.sys.entity.SysLoginInforEntity;
import com.gr.utils.IOUtil;
import com.gr.utils.PageUtils;
import com.gr.validator.ValidatorUtils;
import com.gr.validator.group.AddGroup;
import com.gr.validator.group.UpdateGroup;
import com.gr.sys.entity.SysFeedbackEntity;
import com.gr.sys.service.SysFeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 意见反馈
 *
 * @author liangc
 * @date 2019-09-09 10:15:23
 */
@Slf4j
@Api(tags = "意见反馈")
@RestController
@RequestMapping("sys/feedback")
public class SysFeedbackController extends AbstractController
{
    @Autowired
    private SysFeedbackService sysFeedbackService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysFeedbackService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{feedbackId}")
    @ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
    public R info(@PathVariable("feedbackId") Long feedbackId) {
        SysFeedbackEntity feedback = sysFeedbackService.getById(feedbackId);

        return R.ok().put("feedback", feedback);
    }

    /**
     * 保存
     */
    @SysLog("意见反馈新增")
    @PostMapping("/save")
    @ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
    public R save(@RequestBody SysFeedbackEntity feedback) {
        ValidatorUtils.validateEntity(feedback, AddGroup.class);

        feedback.setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now());
        sysFeedbackService.save(feedback);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("意见反馈修改")
    @PostMapping("/update")
    @ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
    public R update(@RequestBody SysFeedbackEntity feedback) {
        ValidatorUtils.validateEntity(feedback, UpdateGroup.class);

        feedback.setUserId(getLoginUserId()).setCreator(getLoginUser().getUsername()).setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now());
        sysFeedbackService.saveOrUpdate(feedback);

        return R.ok();
    }

    /**
     * 回复
     */
    @SysLog("意见反馈回复")
    @PostMapping("/reply")
    @RequiresPermissions("sys:feedback:update")
    @ApiOperation(value = "回复", notes = "@RequestBody接收实体类")
    public R reply(@RequestBody SysFeedbackEntity feedback) {
        ValidatorUtils.validateEntity(feedback, UpdateGroup.class);

        feedback.setStatus(Constants.SUPER_ADMIN).setUpdateTime(LocalDateTime.now());
        sysFeedbackService.updateById(feedback);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("意见反馈删除")
    @PostMapping("/delete")
    @RequiresPermissions("sys:feedback:delete")
    @ApiOperation(value = "删除", notes = "@RequestBody接收ids")
    public R delete(@RequestBody Long[] feedbackIds) {
        sysFeedbackService.removeByIds(Arrays.asList(feedbackIds));

        return R.ok();
    }

    /**
     * 导出
     */
    @PostMapping("/export")
    @ApiOperation(value = "导出", notes = "RequestParam接收Map参数")
    @RepeatSubmit
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Workbook workbook = null;
        String title = "意见反馈";
        try{
            ExportParams exportParams = new ExportParams(title, null);
            workbook = ExcelExportUtil.exportBigExcel(exportParams, SysFeedbackEntity.class, new IExcelExportServer() {
                @Override
                public List<Object> selectListForExcelExport(Object o, int page) {
                    List<Object> list = new ArrayList<>();
                    params.put("page", String.valueOf(page));
                    params.put("limit", String.valueOf(o));
                    PageUtils pageUtils = sysFeedbackService.queryPage(params);
                    pageUtils.getList().forEach(item -> {
                        list.add(item);
                    });

                    return list;
                }
            }, 10000);

            IOUtil.excel(response, request, workbook, title);

        } catch (Exception e) {
            log.error("意见反馈导出失败{}", e.getMessage());
        }finally {
            if(workbook != null){
                workbook.close();
            }
        }
    }
}
