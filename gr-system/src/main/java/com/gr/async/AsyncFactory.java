package com.gr.async;

import com.gr.constant.Constants;
import com.gr.ip.AddressUtil;
import com.gr.ip.IPUtils;
import com.gr.utils.ServletUtil;
import com.gr.utils.SpringUtil;
import com.gr.sys.entity.SysLoginInforEntity;
import com.gr.sys.service.SysLoginInforService;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.TimerTask;

/**
 * 异步工厂
 * @author liangc
 * @date 2020-03-12 16:40
 */
@Slf4j
public class AsyncFactory {

	/**
	 * 登陆日志
	 *
	 * @param username 用户名
	 * @param status 状态
	 * @param message 消息
	 * @param args 列表
	 * @return 任务task
	 */
	public static TimerTask recordLogininfor(final String account, final String username, final Integer status, final String message, final Object... args){
		final cn.hutool.http.useragent.UserAgent userAgent1 = cn.hutool.http.useragent.UserAgentUtil.parse(ServletUtil.getRequest().getHeader("User-Agent"));
		final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getRequest().getHeader("User-Agent"));
		String ip = IPUtils.getIpAddr();
		return new TimerTask(){
			@Override
			public void run(){
				// 获取客户端操作系统
				String os = userAgent.getOperatingSystem().getName();
				// 获取客户端浏览器
				String browser = userAgent.getBrowser().getName();

				SysLoginInforEntity logininfor = new SysLoginInforEntity();
				logininfor.setUsername(username);
				logininfor.setAccount(account);
				logininfor.setIpaddr(ip.equals(Constants.IpAddr1) ? Constants.IpAddr2 : ip);
				logininfor.setLocation(SpringUtil.getBean(AddressUtil.class).getAddressResolution(logininfor.getIpaddr()));
				logininfor.setBrowser(browser);
				logininfor.setMobile(userAgent1.isMobile() == true ? Constants.ZERO : Constants.SUPER_ADMIN);
				logininfor.setOs(os);
				logininfor.setMessage(message);
				logininfor.setStatus(status);
				logininfor.setCreateTime(LocalDateTime.now());

				SpringUtil.getBean(SysLoginInforService.class).save(logininfor);
			}
		};
	}

}
