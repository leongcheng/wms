package com.gr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysUserPostDao;
import com.gr.sys.entity.SysUserPostEntity;
import com.gr.sys.service.SysUserPostService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("sysUserPostService")
public class SysUserPostServiceImpl extends ServiceImpl<SysUserPostDao, SysUserPostEntity> implements SysUserPostService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String information = (String) params.get("information");

        IPage<SysUserPostEntity> page = this.page(
                new Query<SysUserPostEntity>().getPage(params),
                new QueryWrapper<SysUserPostEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 新增用户
     * @param userId
     * @param postIdList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateUserPost(Long userId, List<Long> postIdList) {

        // 先删除用户与岗位关系
        this.remove(new QueryWrapper<SysUserPostEntity>().eq("user_id", userId));

        if (CollectionUtils.isEmpty(postIdList)){
            return true;
        }

        // 保存用户与岗位关系
        List<SysUserPostEntity> list = new ArrayList<>(postIdList.size());
        for (Long roleId : postIdList) {
            SysUserPostEntity SysUserPost = new SysUserPostEntity();
            SysUserPost.setUserId(userId);
            SysUserPost.setPostId(roleId);

            list.add(SysUserPost);
        }
        return this.saveBatch(list);
    }

    /**
     * 登录用户
     * @param userId
     * @return
     */
    @Override
    public SysUserPostEntity loginUserPost(Long userId) {
        return this.getOne(new QueryWrapper<SysUserPostEntity>().eq("user_id", userId));
    }

}
