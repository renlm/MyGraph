package cn.renlm.graph.controller.pub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.service.PubDocService;

/**
 * 公共文档
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/pub/doc")
public class PubDocController {

	@Autowired
	private PubDocService pubDocService;

	/**
	 * 分享（项目）
	 * 
	 * @param model
	 * @param shareUuid
	 * @return
	 */
	@GetMapping("/s/{shareUuid}")
	public String docShare(ModelMap model, @PathVariable String shareUuid) {
		return "pub/docShare";
	}

	/**
	 * 分享（分类）
	 * 
	 * @param model
	 * @param shareUuid
	 * @return
	 */
	@GetMapping("/c/{shareUuid}")
	public String docShareCategory(ModelMap model, @PathVariable String shareUuid) {
		return "pub/docShareCategory";
	}

	/**
	 * 获取树形结构
	 * 
	 * @param shareUuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(String shareUuid) {
		List<Tree<Long>> tree = pubDocService.getTree(shareUuid);
		return tree;
	}

	/**
	 * 分享（文档）
	 * 
	 * @param model
	 * @param shareUuid
	 * @param docCategoryUuid
	 * @return
	 */
	@GetMapping("/m/{shareUuid}")
	public String docShareMarkdown(ModelMap model, @PathVariable String shareUuid, String docCategoryUuid) {
		return "pub/docShareMarkdown";
	}
}