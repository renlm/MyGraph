package cn.renlm.graph.controller.doc;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Mxgraph;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.service.IDocCategoryCollectService;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.service.IGraphService;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.entity.MarkdownHistory;
import cn.renlm.graph.modular.markdown.service.IMarkdownHistoryService;
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
	private IMarkdownHistoryService iMarkdownHistoryService;

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Autowired
	private IDocCategoryCollectService iDocCategoryCollectService;

	@Resource
	private IGraphService iGraphService;

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
		if (ObjectUtil.isNotEmpty(markdown) && StrUtil.isNotBlank(markdown.getGraphUuid())) {
			model.put("Mxgraph", Mxgraph.values());
			model.put("graph", iGraphService.getOne(Wrappers.<Graph>lambdaQuery().func(wrapper -> {
				wrapper.select(Graph::getUuid, Graph::getCategoryCode, Graph::getCategoryName);
				wrapper.eq(Graph::getUuid, markdown.getGraphUuid());
				wrapper.eq(Graph::getVersion, markdown.getGraphVersion());
			})));
		}
		return "doc/markdown";
	}

	/**
	 * 数据表格
	 * 
	 * @param model
	 * @param uuid
	 * @param version
	 * @param name
	 * @return
	 */
	@RequestMapping("/luckysheet")
	public String luckysheet(ModelMap model, String uuid, Integer version, String name) {
		Markdown markdown = new Markdown();
		markdown.setUuid(uuid);
		markdown.setName(name);
		markdown.setVersion(version);
		if (StrUtil.isNotBlank(uuid)) {
			if (version == null) {
				Markdown entity = iMarkdownService.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, uuid));
				if (ObjectUtil.isNotEmpty(entity)) {
					BeanUtil.copyProperties(entity, markdown);
				}
			} else {
				MarkdownHistory history = iMarkdownHistoryService
						.getOne(Wrappers.<MarkdownHistory>lambdaQuery().func(wrapper -> {
							wrapper.eq(MarkdownHistory::getMarkdownUuid, uuid);
							wrapper.eq(MarkdownHistory::getVersion, version);
						}));
				if (ObjectUtil.isNotEmpty(history)) {
					BeanUtil.copyProperties(history, markdown);
					markdown.setId(history.getMarkdownId());
					markdown.setUuid(history.getMarkdownUuid());
				}
			}
		}
		model.put("markdown", markdown);
		return "doc/luckysheet";
	}
}