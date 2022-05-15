package cn.renlm.graph.controller.markdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.service.IMarkdownService;
import cn.renlm.graph.response.Result;

/**
 * Markdown 文档
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/markdown")
public class MarkdownController {

	@Autowired
	private IMarkdownService iMarkdownService;

	/**
	 * 编辑器
	 * 
	 * @param model
	 * @param uuid
	 * @param name
	 * @return
	 */
	@GetMapping("/editor")
	public String editor(ModelMap model, String uuid, String name) {
		Markdown markdown = new Markdown();
		markdown.setUuid(uuid);
		markdown.setName(name);
		if (StrUtil.isNotBlank(uuid)) {
			Markdown entity = iMarkdownService.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, uuid));
			if (ObjectUtil.isNotEmpty(entity)) {
				BeanUtil.copyProperties(entity, markdown);
			}
		}
		model.put("markdown", markdown);
		return "markdown/editor";
	}

	/**
	 * 查看器
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@GetMapping("/viewer")
	public String viewer(ModelMap model, String uuid) {
		Markdown markdown = new Markdown();
		markdown.setUuid(uuid);
		if (StrUtil.isNotBlank(uuid)) {
			Markdown entity = iMarkdownService.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, uuid));
			if (ObjectUtil.isNotEmpty(entity)) {
				BeanUtil.copyProperties(entity, markdown);
			}
		}
		model.put("markdown", markdown);
		return "markdown/viewer";
	}

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/save")
	public Result<?> ajaxSave(Authentication authentication, Markdown form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iMarkdownService.ajaxSave(user, form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}