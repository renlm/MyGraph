package cn.renlm.graph.controller.pub;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.dto.DocShareUser;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.service.IGraphService;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.service.IMarkdownService;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.service.ISysFileService;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.service.PubDocService;
import cn.renlm.graph.util.TreeExtraUtil;

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

	@Resource
	private IGraphService iGraphService;

	@Autowired
	private ISysFileService iSysFileService;

	/**
	 * 验证密码
	 * 
	 * @param request
	 * @param model
	 * @param shareUuid
	 * @param password
	 * @return
	 */
	@RequestMapping("/verifyPassword")
	public String verifyPassword(HttpServletRequest request, ModelMap model, String shareUuid, String password) {
		Result<?> result = DocShareUser.verifyPassword(request, model, shareUuid, password);
		if (result.isSuccess()) {
			return "redirect:/pub/doc/s/" + shareUuid;
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
		List<Tree<Long>> tree = pubDocService.getTree(request, shareUuid);
		List<Tree<Long>> nodes = TreeExtraUtil.getAllNodes(tree);
		model.put("tree", tree);
		model.put("nodes", CollUtil.size(nodes));
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

	/**
	 * 图形预览
	 * 
	 * @param request
	 * @param model
	 * @param shareUuid
	 * @param uuid
	 * @return
	 */
	@GetMapping("/g/{shareUuid}")
	public String docShareGraph(HttpServletRequest request, ModelMap model, @PathVariable String shareUuid,
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
		Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, markdown.getGraphUuid()));
		graph.setXml(StrUtil.isBlank(graph.getXml()) ? null : Base64.encodeUrlSafe(graph.getXml()));
		model.put("graphJson", JSONUtil.toJsonStr(graph));
		return "graph/viewer";
	}

	/**
	 * 数据表格
	 * 
	 * @param request
	 * @param model
	 * @param shareUuid
	 * @param uuid
	 * @return
	 */
	@GetMapping("/dt/{shareUuid}")
	public String docShareDataTable(HttpServletRequest request, ModelMap model, @PathVariable String shareUuid,
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
		model.put("markdown", markdown);
		return "doc/luckysheet";
	}

	/**
	 * 下载（带参inline为预览）
	 * 
	 * @param request
	 * @param response
	 * @param fileId
	 * @throws IOException
	 */
	@GetMapping("/f/{fileId}")
	public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable String fileId)
			throws IOException {
		boolean inline = request.getParameterMap().containsKey("inline");
		String openStyle = inline ? "inline" : "attachment";
		SysFile file = iSysFileService.getOne(Wrappers.<SysFile>lambdaQuery().eq(SysFile::getFileId, fileId));
		String filename = URLEncoder.encode(file.getOriginalFilename(), "UTF-8");
		response.setHeader("Content-Type", file.getFileType());
		response.setHeader("Content-Disposition", openStyle + ";fileName=" + filename);
		try (ServletOutputStream os = response.getOutputStream()) {
			os.write(file.getFileContent());
		}
	}
}