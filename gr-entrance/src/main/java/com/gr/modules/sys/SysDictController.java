package com.gr.modules.sys;

import com.gr.constant.R;
import com.gr.utils.PageUtils;
import com.gr.annotation.SysLog;
import com.gr.sys.entity.SysDictEntity;
import com.gr.sys.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;


/**
 * 数据字典
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Api(tags = "数据字典")
@RestController
@RequestMapping("sys/dict")
public class SysDictController
{
	@Autowired
	private SysDictService sysDictService;

	/**
	 * 列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:dict:list")
	@ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = sysDictService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@GetMapping("/info/{dictId}")
	@RequiresPermissions("sys:dict:info")
	@ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
	public R info(@PathVariable("dictId") Long dictId) {
		SysDictEntity sysDict = sysDictService.getById(dictId);

		return R.ok().put("sysDict", sysDict);
	}

	/**
	 * 保存
	 */
	@SysLog("保存词典")
	@PostMapping("/save")
	@RequiresPermissions("sys:dict:save")
	@ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
	public R save(@RequestBody SysDictEntity sysDict) {

		sysDict.setCreateTime(LocalDateTime.now());

		sysDictService.save(sysDict);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@SysLog("修改词典")
	@PostMapping("/update")
	@RequiresPermissions("sys:dict:update")
	@ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
	public R update(@RequestBody SysDictEntity sysDict) {
		sysDictService.updateById(sysDict);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@SysLog("删除词典")
	@PostMapping("/delete")
	@RequiresPermissions("sys:dict:delete")
	@ApiOperation(value = "删除", notes = "@RequestBody接收ids")
	public R delete(@RequestBody Long[] ids) {
		sysDictService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
