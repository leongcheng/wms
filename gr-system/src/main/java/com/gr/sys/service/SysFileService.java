package com.gr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysFileEntity;

import java.util.Map;

/**
 * 文件上传记录表
 *
 * @author liangc
 * @date 2022/1/13 14:58
 */
public interface SysFileService extends IService<SysFileEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteFile(String fileId);
}

