package com.gr.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gr.constant.Query;
import com.gr.fdfs.FastDFSClient;
import com.gr.utils.PageUtils;
import com.gr.sys.dao.SysFileDao;
import com.gr.sys.entity.SysFileEntity;
import com.gr.sys.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysFileService")
public class SysFileServiceImpl extends ServiceImpl<SysFileDao, SysFileEntity> implements SysFileService {

	@Autowired
	private FastDFSClient fastDFSClient;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String information = (String) params.get("information");
		IPage<SysFileEntity> page = this.page(
				new Query<SysFileEntity>().getPage(params),
				new LambdaQueryWrapper<SysFileEntity>()
						.like(StringUtils.isNotBlank(information), SysFileEntity::getName, information).or()
						.like(StringUtils.isNotBlank(information), SysFileEntity::getRemark, information).or()
						.like(StringUtils.isNotBlank(information), SysFileEntity::getFileId, information)
						.orderByDesc(SysFileEntity::getCreateTime)
		);

		return new PageUtils(page);
	}

	@Override
	@Async("threadPoolTaskExecutor")
	public void deleteFile(String fileId) {
		List<SysFileEntity> fileList = this.list(new LambdaQueryWrapper<SysFileEntity>().eq(SysFileEntity::getFileId, fileId));
		for(SysFileEntity item: fileList){
			//删除服务器上的文件
			fastDFSClient.deleteFile(item.getUrl());
			break;
		}
	}

}
