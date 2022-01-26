package com.gr.modules.monitor.controller;

import com.gr.constant.R;
import com.gr.modules.monitor.domain.Server;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


/**
 * 
 * 服务监控
 * @author liangc
 * @date 2020/04/19 11:14:32
 *
 */
@Controller
@RequestMapping("/sys/server")
public class ServiceController{
	
	@RequestMapping("/info")
	@ResponseBody
	public R info(@RequestParam Map<String ,Object> param) {
		Server server = new Server();
		try {
			server.copyTo();
		} catch (Exception e) {
			return R.error("获取服务器信息异常");
		}
		return R.ok().put("server", server);
	}
}
