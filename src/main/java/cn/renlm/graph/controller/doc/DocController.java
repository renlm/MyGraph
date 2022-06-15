package cn.renlm.graph.controller.doc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.service.IMarkdownService;

/**
 * 文档管理
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/doc")
public class DocController {

	@Autowired
	private IMarkdownService iMarkdownService;

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	/**
	 * 知识文库
	 * 
	 * @return
	 */
	@GetMapping("/lib")
	public String lib() {
		return "doc/lib";
	}

	/**
	 * 在线文档
	 * 
	 * @param model
	 * @param docProjectUuid
	 * @param uuid
	 * @param name
	 * @return
	 */
	@GetMapping("/markdown")
	public String markdown(ModelMap model, String docProjectUuid, String uuid, String name) {
		Markdown markdown = iMarkdownService.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, uuid));
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, uuid));
		DocCategory docCategory = iDocCategoryService
				.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, uuid));
		List<DocCategory> fathers = iDocCategoryService.findFathers(docProjectUuid, docCategory.getId());
		model.put("markdown", markdown);
		model.put("docProject", docProject);
		model.put("docCategory", docCategory);
		model.put("fathers", fathers);
		return "doc/markdown";
	}
}