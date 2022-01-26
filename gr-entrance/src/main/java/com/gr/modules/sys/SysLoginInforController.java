package com.gr.modules.sys;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.gr.constant.R;
import com.gr.utils.IOUtil;
import com.gr.utils.PageUtils;
import com.gr.annotation.RepeatSubmit;
import com.gr.sys.entity.SysLoginInforEntity;
import com.gr.sys.service.SysLoginInforService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 登录日志
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Slf4j
@Api(tags = "登录日志")
@RestController
@RequestMapping("sys/loginInfor")
public class SysLoginInforController
{
    @Autowired
    private SysLoginInforService sysLoginInforService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:logininfor:list")
    @ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysLoginInforService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("sys:logininfor:info")
    @ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
    public R info(@PathVariable("id") Long id) {
        SysLoginInforEntity sysLoginInfor = sysLoginInforService.getById(id);

        return R.ok().put("sysLoginInfor", sysLoginInfor);
    }


    /**
     * 导出
     */
    @PostMapping("/export")
    @ApiOperation(value = "导出", notes = "RequestParam接收Map参数")
    @RepeatSubmit
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Workbook workbook = null;
        String title = "登录日志";
        try{
            ExportParams exportParams = new ExportParams(title, null);
            workbook = ExcelExportUtil.exportBigExcel(exportParams, SysLoginInforEntity.class, new IExcelExportServer() {
                @Override
                public List<Object> selectListForExcelExport(Object o, int page) {
                    List<Object> list = new ArrayList<>();
                    params.put("page", String.valueOf(page));
                    params.put("limit", String.valueOf(o));
                    PageUtils pageUtils = sysLoginInforService.queryPage(params);
                    pageUtils.getList().forEach(item -> {
                        list.add(item);
                    });

                    return list;
                }
            }, 10000);

            IOUtil.excel(response, request, workbook, title);

        } catch (Exception e) {
            log.error("登录日志导出失败{}", e.getMessage());
        }finally {
            if(workbook != null){
                workbook.close();
            }
        }
    }
}
