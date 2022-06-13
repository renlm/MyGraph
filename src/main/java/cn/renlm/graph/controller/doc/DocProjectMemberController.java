package cn.renlm.graph.controller.doc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文档项目-成员
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/doc/projectMember")
public class DocProjectMemberController {

	/**
	 * 成员管理
	 * 
	 * @return
	 */
	@GetMapping("/dialog")
	public String dialog() {
		return "doc/projectMemberDialog";
	}
}