package com.gr.modules.sys;

import com.gr.utils.PageUtils;
import com.gr.sys.entity.SysUserEntity;
import org.apache.shiro.SecurityUtils;

import java.util.List;
import java.util.Map;

/**
 * Controller公共组件
 * 
 */
public abstract class AbstractController {

	protected SysUserEntity getLoginUser() {
		return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
	}

	protected Long getLoginUserId() { return getLoginUser().getUserId(); }

	/**
	 * 响应请求分页数据
	 */
	protected PageUtils getDataTable(List<?> list, Map<String, Object> params)
	{
		int limit = Integer.parseInt(String.valueOf(params.get("limit")));
		int currPage = Integer.parseInt(String.valueOf(params.get("page")));

		List<?> subList;
		int totalCount = list.size();
		if (list.size() <= limit)
		{
			return new PageUtils(list, totalCount, limit, currPage);
		}
		else if (limit * currPage <= totalCount)
		{
			subList = list.subList(limit * currPage - limit, limit * currPage);
		}
		else
		{
			subList = list.subList(limit * currPage - limit, list.size());
		}

		return new PageUtils(subList, totalCount, limit, currPage);
	}
}
