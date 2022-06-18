package cn.renlm.graph.controller.pub;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 公共文档
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/pub/doc")
public class PubDocController {

	/**
	 * 分享（项目）
	 * 
	 * @return
	 */
	@GetMapping("/s/{uuid}")
	public String docShare() {
		return "pub/docShare";
	}

	/**
	 * 分享（分类）
	 * 
	 * @return
	 */
	@GetMapping("/c/{uuid}")
	public String docShareCategory() {
		return "pub/docShareCategory";
	}

	/**
	 * 分享（文档）
	 * 
	 * @return
	 */
	@GetMapping("/m/{uuid}")
	public String docShareMarkdown() {
		return "pub/docShareMarkdown";
	}
}