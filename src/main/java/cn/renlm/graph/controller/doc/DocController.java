package cn.renlm.graph.controller.doc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.service.IDocCategoryCollectService;
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

	@Autowired
	private IDocCategoryCollectService iDocCategoryCollectService;

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
	 * @param authentication
	 * @param model
	 * @param docProjectUuid
	 * @param uuid
	 * @param name
	 * @return
	 */
	@GetMapping("/markdown")
	public String markdown(Authentication authentication, ModelMap model, String docProjectUuid, String uuid,
			String name) {
		User user = (User) authentication.getPrincipal();
		Markdown markdown = iMarkdownService.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, uuid));
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		DocCategory docCategory = iDocCategoryService
				.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, uuid));
		List<DocCategory> fathers = iDocCategoryService.findFathers(docProjectUuid, docCategory.getId());
		boolean isCollected = iDocCategoryCollectService.isCollected(user, docCategory.getId());
		Integer role = iDocProjectService.findRole(user, docProject.getId());
		model.put("markdown", markdown);
		model.put("docProject", docProject);
		model.put("docCategory", docCategory);
		model.put("fathers", fathers);
		model.put("isCollected", isCollected);
		model.put("role", role);
		return "doc/markdown";
	}
}