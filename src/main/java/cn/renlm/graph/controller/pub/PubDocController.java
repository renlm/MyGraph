package cn.renlm.graph.controller.pub;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.DocShareUser;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.service.IMarkdownService;
import cn.renlm.graph.response.Result;
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
	 * 验证密码
	 * 
	 * @param request
	 * @param model
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping("/verifyPassword")
	public String verifyPassword(HttpServletRequest request, ModelMap model, String username, String password) {
		Result<?> result = DocShareUser.verifyPassword(request, model, username, password);
		if (result.isSuccess()) {
			return "redirect:/pub/doc/s/" + username;
		} else {
			model.put("result", result);
			return "pub/docSharePasswd";
		}
	}

	/**
	 * 分享（项目）
	 * 
	 * @param request
	 * @param model
	 * @param shareUuid
	 * @return
	 */
	@GetMapping("/s/{shareUuid}")
	public String docShare(HttpServletRequest request, ModelMap model, @PathVariable String shareUuid) {
		DocCategoryShareDto docCategoryShare = pubDocService.getDocCategoryShare(shareUuid);
		model.put("shareUuid", shareUuid);
		model.put("docCategoryShare", docCategoryShare);
		if (NumberUtil.equals(docCategoryShare.getShareType(), 2)) {
			DocShareUser user = DocShareUser.getInfo(request, shareUuid);
			if (user == null) {
				return "pub/docSharePasswd";
			} else if (!StrUtil.equals(shareUuid, user.getShareUuid())) {
				throw new RuntimeException("行为异常");
			}
		}
		return "pub/docShare";
	}

	/**
	 * 分享（分类）
	 * 
	 * @param request
	 * @param model
	 * @param shareUuid
	 * @return
	 */
	@GetMapping("/c/{shareUuid}")
	public String docShareCategory(HttpServletRequest request, ModelMap model, @PathVariable String shareUuid) {
		DocCategoryShareDto docCategoryShare = pubDocService.getDocCategoryShare(shareUuid);
		model.put("shareUuid", shareUuid);
		model.put("docCategoryShare", docCategoryShare);
		if (NumberUtil.equals(docCategoryShare.getShareType(), 2)) {
			DocShareUser user = DocShareUser.getInfo(request, shareUuid);
			if (user == null) {
				return "pub/docSharePasswd";
			} else if (!StrUtil.equals(shareUuid, user.getShareUuid())) {
				throw new RuntimeException("行为异常");
			}
		}
		return "pub/docShareCategory";
	}

	/**
	 * 获取树形结构
	 * 
	 * @param request
	 * @param shareUuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/{shareUuid}/getTree")
	public List<Tree<Long>> getTree(HttpServletRequest request, @PathVariable String shareUuid) {
		List<Tree<Long>> tree = pubDocService.getTree(request, shareUuid);
		return tree;
	}

	/**
	 * 分享（文档）
	 * 
	 * @param request
	 * @param model
	 * @param shareUuid
	 * @param uuid
	 * @return
	 */
	@GetMapping("/m/{shareUuid}")
	public String docShareMarkdown(HttpServletRequest request, ModelMap model, @PathVariable String shareUuid,
			String uuid) {
		DocCategoryShareDto docCategoryShare = pubDocService.getDocCategoryShare(shareUuid);
		model.put("shareUuid", shareUuid);
		model.put("docCategoryShare", docCategoryShare);
		if (NumberUtil.equals(docCategoryShare.getShareType(), 2)) {
			DocShareUser user = DocShareUser.getInfo(request, shareUuid);
			if (user == null) {
				return "pub/docSharePasswd";
			} else if (!StrUtil.equals(shareUuid, user.getShareUuid())) {
				throw new RuntimeException("行为异常");
			}
		}
		Markdown markdown = iMarkdownService.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, uuid));
		DocCategory docCategory = iDocCategoryService
				.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, uuid));
		List<DocCategory> fathers = iDocCategoryService.findFathers(docCategoryShare.getDocProjectUuid(),
				docCategory.getId());
		model.put("markdown", markdown);
		model.put("docCategory", docCategory);
		model.put("fathers", fathers);
		return "pub/docShareMarkdown";
	}
}