package com.gr.modules.sys;

import com.gr.constant.Constants;
import com.gr.constant.R;
import com.gr.exception.RRException;
import com.gr.exception.ResultEnum;
import com.gr.exception.ResultException;
import com.gr.validator.ValidatorUtils;
import com.gr.validator.group.AddGroup;
import com.gr.validator.group.UpdateGroup;
import com.gr.annotation.RepeatSubmit;
import com.gr.annotation.SysLog;
import com.gr.redis.RedisUtils;
import com.gr.sys.entity.SysMenuEntity;
import com.gr.sys.entity.TreeSelect;
import com.gr.sys.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 菜单表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("sys/menu")
public class SysMenuController extends AbstractController
{
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private RedisUtils redisUtils;

	/**
	 * 所有菜单列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:menu:list")
	@ApiOperation(value = "列表", notes = "RequestParam接收Map参数")
	public R list(@RequestParam Map<String, Object> params){
		//查询所有菜单
		List<SysMenuEntity> menuList = sysMenuService.getAllMenuList();

		//上级菜单名称
		Map<Long, SysMenuEntity> menuMap = new HashMap<>(12);
		for (SysMenuEntity menu: menuList) {
			menuMap.put(menu.getMenuId(), menu);
		}
		for (SysMenuEntity menu1 : menuList) {
			for (SysMenuEntity menu2: menu1.getChildren()) {
				SysMenuEntity parent = menuMap.get(menu2.getParentId());
				if (Objects.nonNull(parent)) {
					menu2.setParentName(parent.getTitle());
				}
			}
		}

		return R.ok().put("menuList", menuList);
	}

	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@GetMapping("/select")
	@ApiOperation(value = "选择菜单", notes = "RequestParam接收Map参数")
	@RequiresPermissions("sys:menu:select")
	public R select(@RequestParam Map<String, Object> params){
		//查询列表数据
		List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();

		//添加顶级菜单
		SysMenuEntity root = new SysMenuEntity();
		root.setMenuId(0L);
		root.setTitle("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);

		return R.ok().put("menuList", menuList);
	}

	/**
	 * 网页菜单
	 */
	@GetMapping("/nav")
	@ApiOperation(value = "网页菜单", notes = "RequestParam接收Map参数")
	public R nav(@RequestParam Map<String, Object> params) {
		if (getLoginUser() == null){
			throw new ResultException(ResultEnum.ERR013);
		}
		List<SysMenuEntity> menuList = sysMenuService.getMenuListByUserId(getLoginUserId());

		return R.ok().put("menuList", menuList);
	}

	/**
	 * 手机菜单
	 */
	@GetMapping("/mobile")
	@ApiOperation(value = "手机菜单", notes = "RequestParam接收Map参数")
	public R mobile(@RequestParam Map<String, Object> params) {
		if (getLoginUser() == null){
			throw new ResultException(ResultEnum.ERR013);
		}
		List<SysMenuEntity> menuList = sysMenuService.getMenuListByUserIdMobile(getLoginUserId());

		return R.ok().put("menuList", menuList);
	}

	/**
	 * 查询菜单树结构
	 */
	@GetMapping("/menuTree")
	@RequiresPermissions("sys:menu:tree")
	@ApiOperation(value = "查询菜单树结构", notes = "RequestParam接收Map参数")
	public R menuTree(@RequestParam Map<String, Object> params) {
		List<SysMenuEntity> menuTrees = sysMenuService.getAllMenuList();

		return R.ok().put("menuTrees", menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList()));
	}

	/**
	 * 加载对应角色菜单列表树
	 */
	@GetMapping(value = "/roleMenuTreeSelect/{roleId}")
	@RequiresPermissions("sys:menu:select")
	@ApiOperation(value = "加载对应角色菜单列表树", notes = "RequestParam接收Map参数")
	public R roleMenuTreeSelect(@PathVariable("roleId") Long roleId)
	{
		//List<SysMenuEntity> menus = sysMenuService.getMenuListByUserId(getLoginUserId());
		List<SysMenuEntity> menuTrees = sysMenuService.getAllMenuList();

		Map<String, Object> map = new HashMap<>();
		map.put("checkedKeys", sysMenuService.selectMenuListByRoleId(roleId));//根据角色ID查询菜单
		map.put("menus", menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList()));//构建前端所需要下拉树结构

		return R.ok().put("map", map);
	}

	/**
	 * 信息
	 */
	@GetMapping("/info/{menuId}")
	@RequiresPermissions("sys:menu:info")
	@ApiOperation(value = "信息", notes = "@PathVariable接收主键id")
	public R info(@PathVariable("menuId") Long menuId) {
		SysMenuEntity sysMenu = sysMenuService.getById(menuId);

		return R.ok().put("sysMenu", sysMenu);
	}

	/**
	 * 保存
	 */
	@SysLog("新增菜单")
	@PostMapping("/save")
	@RequiresPermissions("sys:menu:save")
	@ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
	@RepeatSubmit
	public R save(@RequestBody SysMenuEntity sysMenu) {
		ValidatorUtils.validateEntity(sysMenu, AddGroup.class);
		// 数据参数校验
		verifyForm(sysMenu);

		sysMenuService.save(sysMenu.setCreateTime(LocalDateTime.now()));

		return R.ok();
	}

	/**
	 * 修改
	 */
	@SysLog("修改菜单")
	@PostMapping("/update")
	@RequiresPermissions("sys:menu:update")
	@ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
	@RepeatSubmit
	public R update(@RequestBody SysMenuEntity sysMenu) {
		ValidatorUtils.validateEntity(sysMenu, UpdateGroup.class);
		// 数据参数校验
		verifyForm(sysMenu);

		boolean result = sysMenuService.updateById(sysMenu);
		if (result) {
			// 清理权限缓存
			redisUtils.delKeys(Constants.PREFIX_SHIRO_CACHE + "*");

			return R.ok().put("menuId", sysMenu.getMenuId());
		}
		return R.error();
	}

	/**
	 * 删除
	 */
	@SysLog("删除菜单")
	@GetMapping("/delete")
	@RequiresPermissions("sys:menu:delete")
	@ApiOperation(value = "删除", notes = "@PathVariable接收主键id")
	@RepeatSubmit
	public R delete(Long menuId) {
		SysMenuEntity sysMenu = sysMenuService.getById(menuId);
		if (sysMenu == null) {
			return R.error("菜单不存在，请刷新");
		}

		if (menuId == 4 || sysMenu.getParentId() == 4) {
			return R.error("菜单管理相关功能，不能删除");
		}

		// 判断是否有子节点
		List<SysMenuEntity> menuList = sysMenuService.getMenuListByParentId(menuId);
		if (menuList.size() > 0) {
			return R.error("请先删除子节点");
		}

		//删除关联menuId
		boolean result = sysMenuService.deleteByMenuId(menuId);
		if (result) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysMenuEntity menu) {
		if (StringUtils.isBlank(menu.getTitle())) {
			throw new RRException("名称不能为空");
		}

		if (menu.getParentId() == null) {
			throw new RRException("上级不能为空");
		}

		if (menu.getType() == Constants.MenuType.MENU.getValue()) {
			if (StringUtils.isBlank(menu.getUrl())) {
				throw new RRException("菜单URL不能为空");
			}
		}

		// 上级菜单类型
		int parentType = Constants.MenuType.CATALOG.getValue();
		if (menu.getParentId() != 0) {
			SysMenuEntity parentMenu = sysMenuService.getById(menu.getParentId());
			parentType = parentMenu.getType();
		}

		// 目录、菜单校验
		if (menu.getType() == Constants.MenuType.CATALOG.getValue()
				|| menu.getType() == Constants.MenuType.MENU.getValue()) {
			if (parentType != Constants.MenuType.CATALOG.getValue()) {
				throw new RRException("上级只能为目录类型");
			}
		}

		// 按钮功能校验
		if (menu.getType() == Constants.MenuType.BUTTON.getValue()) {
			if (parentType != Constants.MenuType.MENU.getValue()) {
				throw new RRException("上级能为菜单类型");
			}
		}
	}

}
