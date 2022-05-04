package cn.renlm.graph.controller.sys;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 数据字典
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/dict")
public class SysDictController {

	/**
	 * 字典列表
	 * 
	 * @return
	 */
	@GetMapping
	public String index() {
		return "sys/dict";
	}
}