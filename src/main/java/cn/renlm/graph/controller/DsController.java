package cn.renlm.graph.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 数据源
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/ds")
public class DsController {

	/**
	 * 列表页
	 * 
	 * @return
	 */
	@RequestMapping("/list")
	public String list() {
		return "ds/list";
	}
}