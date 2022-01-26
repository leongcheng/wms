package com.gr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.sys.dao.SysDeptDao;
import com.gr.sys.entity.SysDeptEntity;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.service.SysDeptService;
import com.gr.utils.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("sysDeptService")
public class SysDeptServiceImpl extends ServiceImpl<SysDeptDao, SysDeptEntity> implements SysDeptService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String information = (String) params.get("information");
        SysUserEntity user = (SysUserEntity) SecurityUtils.getSubject().getPrincipal();

        IPage<SysDeptEntity> page = this.page(
                new Query<SysDeptEntity>().getPage(params),
                new QueryWrapper<SysDeptEntity>().ne("parent_id",0)
                        .like(StringUtils.isNotBlank(information), "dept_name", information)
                        //.inSql(user.getUserId() != Constants.SUPER_ADMIN,"dept_id", "select sud.dept_id from sys_user_dept sud where user_id =" + user.getUserId())
        );

        return new PageUtils(page);
    }

    @Override
    public List<SysDeptEntity> queryList(Map<String, Object> params) {
        return baseMapper.queryList(params);
    }

    @Override
    public List<Long> queryDetpIdList(Long parentId) {
        return baseMapper.queryDetpIdList(parentId);
    }

    @Override
    public List<Long> getSubDeptIdList(Long deptId) {
        //公司及子公司ID列表
        List<Long> deptIdList = new ArrayList<>();

        //获取子公司ID
        List<Long> subIdList = queryDetpIdList(deptId);
        getDeptTreeList(subIdList, deptIdList);

        return deptIdList;
    }

    /**
     * 递归
     */
    private void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList) {
        for (Long deptId : subIdList) {
            List<Long> list = queryDetpIdList(deptId);
            if (list.size() > 0) {
                getDeptTreeList(list, deptIdList);
            }

            deptIdList.add(deptId);
        }
    }

    @Override
    public List<SysDeptEntity> getAllDeptList() {

        return this.list(new LambdaQueryWrapper<SysDeptEntity>().orderByAsc(SysDeptEntity::getSort));
    }

    @Override
    public List<SysDeptEntity> buildMenuTreeSelect() {
        //1.查出所有菜单
        List<SysDeptEntity> entities = this.list();

        //2.组装成父子的树状图结构
		List<SysDeptEntity> deptList = entities.stream()
				.filter(depEntity -> depEntity.getParentId() == 0)
				.peek(dep -> dep.setChildren(getChildrens(dep, entities)))
                .sorted(Comparator.comparingInt(SysDeptEntity::getSort)).collect(Collectors.toList());

        return deptList;
    }

    /**
     * 递归寻找所有部门的子菜单
     * @param sysDept
     * @param deptList
     * @return
     */
    private List<SysDeptEntity> getChildrens(SysDeptEntity sysDept, List<SysDeptEntity> deptList) {

        List<SysDeptEntity> children = deptList.stream()
                .filter(depEntity -> depEntity.getParentId() == sysDept.getDeptId())
                .peek(depEntity -> depEntity.setChildren(getChildrens(depEntity, deptList)))
                .sorted(Comparator.comparingInt(SysDeptEntity::getSort)).collect(Collectors.toList());

        return children;
    }

}
