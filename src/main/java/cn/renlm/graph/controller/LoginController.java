package cn.renlm.graph.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 登录
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping
public class LoginController {

	/**
	 * 登录页
	 * 
	 * @return
	 */
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
}