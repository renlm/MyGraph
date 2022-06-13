package cn.renlm.graph.controller.doc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文档管理
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/doc")
public class DocController {

	/**
	 * 知识文库
	 * 
	 * @return
	 */
	@GetMapping("/lib")
	public String list() {
		return "doc/lib";
	}
}