package cn.renlm.graph.controller.markdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.markdown.dto.MarkdownHistoryDto;
import cn.renlm.graph.modular.markdown.service.IMarkdownHistoryService;
import cn.renlm.graph.response.Datagrid;

/**
 * Markdown文档-历史记录
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/markdownHistory")
public class MarkdownHistoryController {

	@Autowired
	private IMarkdownHistoryService iMarkdownHistoryService;

	/**
	 * 历史版本（文档）
	 * 
	 * @param model
	 * @param markdownUuid
	 * @return
	 */
	@RequestMapping("/docVersionsDialog")
	public String docVersionsDialog(ModelMap model, String markdownUuid) {
		model.put("markdownUuid", markdownUuid);
		return "markdown/docVersionsDialog";
	}

	/**
	 * 分页列表（文档）
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/docPage")
	public Datagrid<MarkdownHistoryDto> ajaxDocPage(Authentication authentication, Page<MarkdownHistoryDto> page,
			MarkdownHistoryDto form) {
		User user = (User) authentication.getPrincipal();
		Page<MarkdownHistoryDto> data = iMarkdownHistoryService.findDocPage(page, user, form);
		return Datagrid.of(data);
	}
}