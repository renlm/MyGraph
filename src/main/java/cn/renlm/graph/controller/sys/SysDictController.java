package cn.renlm.graph.controller.sys;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * 数据字典
 * 
 * @author Renlm
 *
 */
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