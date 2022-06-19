package cn.renlm.graph.controller.pub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.service.IMarkdownService;
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

	@Autowired
	private IMarkdownService iMarkdownService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	/**
	 * 分享（项目）
	 * 
	 * @param model
	 * @param shareUuid
	 * @return
	 */
	@GetMapping("/s/{shareUuid}")
	public String docShare(ModelMap model, @PathVariable String shareUuid) {
		DocCategoryShareDto docCategoryShare = pubDocService.getDocCategoryShare(shareUuid);
		model.put("shareUuid", shareUuid);
		model.put("docCategoryShare", docCategoryShare);
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
		DocCategoryShareDto docCategoryShare = pubDocService.getDocCategoryShare(shareUuid);
		model.put("shareUuid", shareUuid);
		model.put("docCategoryShare", docCategoryShare);
		return "pub/docShareCategory";
	}

	/**
	 * 获取树形结构
	 * 
	 * @param shareUuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/{shareUuid}/getTree")
	public List<Tree<Long>> getTree(@PathVariable String shareUuid) {
		List<Tree<Long>> tree = pubDocService.getTree(shareUuid);
		return tree;
	}

	/**
	 * 分享（文档）
	 * 
	 * @param model
	 * @param shareUuid
	 * @param uuid
	 * @return
	 */
	@GetMapping("/m/{shareUuid}")
	public String docShareMarkdown(ModelMap model, @PathVariable String shareUuid, String uuid) {
		Markdown markdown = iMarkdownService.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, uuid));
		DocCategoryShareDto docCategoryShare = pubDocService.getDocCategoryShare(shareUuid);
		DocCategory docCategory = iDocCategoryService
				.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, uuid));
		List<DocCategory> fathers = iDocCategoryService.findFathers(docCategoryShare.getDocProjectUuid(),
				docCategory.getId());
		model.put("shareUuid", shareUuid);
		model.put("markdown", markdown);
		model.put("docCategoryShare", docCategoryShare);
		model.put("docCategory", docCategory);
		model.put("fathers", fathers);
		return "pub/docShareMarkdown";
	}
}