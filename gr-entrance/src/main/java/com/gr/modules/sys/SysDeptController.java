package com.gr.modules.sys;

import com.gr.constant.Constants;
import com.gr.constant.R;
import com.gr.validator.ValidatorUtils;
import com.gr.validator.group.AddGroup;
import com.gr.annotation.SysLog;
import com.gr.sys.entity.SysDeptEntity;
import com.gr.sys.service.SysDeptService;
import com.gr.utils.PageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 部门管理
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/sys/dept")
public class SysDeptController extends AbstractController
{
    @Autowired
    private SysDeptService sysDeptService;

    /**
     * 列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RequiresPermissions("sys:dept:list")
    @ApiOperation(value = "列表",notes = "RequestParam接收Map参数")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysDeptService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 上级部门Id(管理员则为0)
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @RequiresPermissions("sys:dept:list")
    @ApiOperation(value = "上级部门",notes = "上级部门Id(管理员则为0)")
    public R info() {
        long deptId = 0;
        if (getLoginUserId() != Constants.SUPER_ADMIN) {
            List<SysDeptEntity> deptList = sysDeptService.queryList(new HashMap<>());
            Long parentId = null;
            for (SysDeptEntity sysDeptEntity : deptList) {
                if (parentId == null) {
                    parentId = sysDeptEntity.getParentId();
                    continue;
                }

                if (parentId > sysDeptEntity.getParentId().longValue()) {
                    parentId = sysDeptEntity.getParentId();
                }
            }
            deptId = parentId;
        }

        return R.ok().put("deptId", deptId);
    }

    /**
     * 查询部门树结构
     */
    @GetMapping("/deptTree")
    @RequiresPermissions("sys:dept:tree")
    @ApiOperation(value = "查询部门树结构",notes = "RequestParam接收Map参数")
    public R deptTree(@RequestParam Map<String, Object> params) {
        List<SysDeptEntity> deptList = sysDeptService.buildMenuTreeSelect();

        //上级部门名称
        Map<Long, SysDeptEntity> map = new HashMap<>(12);
        for (SysDeptEntity dept: deptList) {
            map.put(dept.getDeptId(), dept);
        }
        for (SysDeptEntity dept1 : deptList) {
          for (SysDeptEntity dept2: dept1.getChildren()){
              SysDeptEntity parent = map.get(dept2.getParentId());
              if (Objects.nonNull(parent)) {
                  dept2.setParentName(parent.getDeptName());
              }
          }
        }

        return R.ok().put("deptList", deptList);
    }


    /**
     * 信息
     */
    @RequestMapping(value = "/info/{deptId}", method = RequestMethod.GET)
    @RequiresPermissions("sys:dept:info")
    @ApiOperation(value = "信息",notes = "@PathVariable接收主键deptId")
    public R info(@PathVariable("deptId") Long deptId) {
        SysDeptEntity sysDept = sysDeptService.getById(deptId);

        return R.ok().put("sysDept", sysDept);
    }

    /**
     * 保存
     */
    @SysLog("新增部门")
    @PostMapping("/save")
    @RequiresPermissions("sys:dept:save")
    @ApiOperation(value = "新增", notes = "@RequestBody接收实体类")
    public R save(@RequestBody SysDeptEntity dept) {
        ValidatorUtils.validateEntity(dept, AddGroup.class);

        sysDeptService.save(dept);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("部门修改")
    @PostMapping("/update")
    @RequiresPermissions("sys:dept:update")
    @ApiOperation(value = "修改", notes = "@RequestBody接收实体类")
    public R update(@RequestBody SysDeptEntity dept) {
        ValidatorUtils.validateEntity(dept, AddGroup.class);

        sysDeptService.updateById(dept);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("部门删除")
    @GetMapping("/delete")
    @RequiresPermissions("sys:dept:delete")
    @ApiOperation(value = "删除",notes = "传入：deptId")
    public R delete(Long deptId) {
        //判断是否有子部门
        List<Long> deptList = sysDeptService.queryDetpIdList(deptId);
        if (deptList.size() > 0) {
            return R.error("请先删除子部门");
        }

        sysDeptService.removeById(deptId);

        return R.ok();
    }

}
