package com.gr.modules.sys;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class PageController {

	/**
	 * 页面路径1
	 */
	@RequestMapping("modules/{moduleName}/{url}.html")
	public String module(@PathVariable("moduleName") String moduleName, @PathVariable("url") String url) {

		return "modules/" + moduleName + "/" + url;
	}

	/**
	 * 页面路径2
	 */
	@RequestMapping("modules/{nav}{menu}/{url}.html")
	public String modules(@PathVariable("nav") String nav, @PathVariable("menu") String menu, @PathVariable("url") String url) {

		return "modules/" + nav + menu + "/" + url;
	}

	/**
	 * 登录
	 */
	@GetMapping("login")
	public String login(){
		return "login.html";
	}

	/**
	 * Describe: 获取主页视图
	 * Param: ModelAndView
	 * Return: 登录视图
	 * */
	@GetMapping(value = {"/", "index"})
	public String index(){
		return "index.html";
	}

	/**
	 * Describe: 获取主页视图
	 * Param: ModelAndView
	 * Return: 主页视图
	 * */
	@GetMapping("console")
	public ModelAndView console()
	{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("console/console");
		return modelAndView;
	}

	/**
	 * Describe:无权限页面
	 * Return:返回403页面
	 * */
	@GetMapping("error/403")
	public ModelAndView noPermission(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error/403");
		return modelAndView;
	}

	/**
	 * Describe:找不带页面
	 * Return:返回404页面
	 * */
	@GetMapping("error/404")
	public ModelAndView notFound(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error/404");
		return modelAndView;
	}

	/**
	 * Describe:异常处理页
	 * Return:返回500界面
	 * */
	@GetMapping("error/500")
	public ModelAndView onException(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error/500");
		return modelAndView;
	}

}
