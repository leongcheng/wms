package com.gr.modules.quartz;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.gr.annotation.RepeatSubmit;
import com.gr.constant.R;
import com.gr.quartz.entity.ScheduleJobLogEntity;
import com.gr.quartz.service.ScheduleJobLogService;
import com.gr.utils.IOUtil;
import com.gr.utils.PageUtils;
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
 * 定时任务日志
 *
 * @author liangc
 * @date 2020-03-20 11:14:32
 */
@Slf4j
@RestController
@RequestMapping("/sys/scheduleLog")
public class ScheduleJobLogController
{
	@Autowired
	private ScheduleJobLogService scheduleJobLogService;
	
	/**
	 * 定时任务日志列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:schedule:log")
	public R list(@RequestParam Map<String, Object> params){

		PageUtils page = scheduleJobLogService.queryPage(params);
		
		return R.ok().put("page", page);
	}
	
	/**
	 * 定时任务日志信息
	 */
	@RequestMapping("/info/{logId}")
	public R info(@PathVariable("logId") Long logId){
		ScheduleJobLogEntity jobLog = scheduleJobLogService.getById(logId);
		
		return R.ok().put("jobLog", jobLog);
	}

	/**
	 * 导出
	 */
	@PostMapping("/export")
	@RepeatSubmit
	public void export(@RequestParam Map<String, Object> params, HttpServletResponse response, HttpServletRequest request) throws IOException {
		Workbook workbook = null;
		String title = "调度任务日志";
		try{
			ExportParams exportParams = new ExportParams(title, null);
			workbook = ExcelExportUtil.exportBigExcel(exportParams, ScheduleJobLogEntity.class, new IExcelExportServer() {
				@Override
				public List<Object> selectListForExcelExport(Object o, int page) {
					List<Object> list = new ArrayList<>();
					params.put("page", String.valueOf(page));
					params.put("limit", String.valueOf(o));
					PageUtils pageUtils = scheduleJobLogService.queryPage(params);
					pageUtils.getList().forEach(item -> {
						list.add(item);
					});

					return list;
				}
			}, 10000);

			IOUtil.excel(response, request, workbook, title);

		} catch (Exception e) {
			log.error("调度任务日志导出失败{}", e.getMessage());
		}finally {
			if(workbook != null){
				workbook.close();
			}
		}
	}

}
