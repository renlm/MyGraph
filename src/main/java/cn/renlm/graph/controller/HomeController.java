package cn.renlm.graph.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping
public class HomeController {

	/**
	 * 欢迎页
	 * 
	 * @return
	 */
	@RequestMapping("/welcome")
	public String welcome() {
		return "home/welcome";
	}

	/**
	 * 工作台
	 * 
	 * @return
	 */
	@RequestMapping("/workbench")
	public String workbench() {
		return "workbench";
	}
}