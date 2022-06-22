package cn.renlm.graph.controller.markdown;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.entity.MarkdownHistory;
import cn.renlm.graph.modular.markdown.service.IMarkdownHistoryService;
import cn.renlm.graph.modular.markdown.service.IMarkdownService;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.service.ISysFileService;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.MyConfigProperties;

/**
 * Markdown文档
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/markdown")
public class MarkdownController {

	@Autowired
	private MyConfigProperties myConfigProperties;

	@Autowired
	private ISysFileService iSysFileService;

	@Autowired
	private IMarkdownService iMarkdownService;

	@Autowired
	private IMarkdownHistoryService iMarkdownHistoryService;

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
		markdown.setVersion(1);
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
	 * @param version
	 * @param name
	 * @return
	 */
	@GetMapping("/viewer")
	public String viewer(ModelMap model, String uuid, Integer version, String name) {
		Markdown markdown = new Markdown();
		markdown.setUuid(uuid);
		markdown.setName(name);
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

	/**
	 * 文件上传
	 * 
	 * @param request
	 * @param authentication
	 * @param file
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/upload")
	public Map<?, Object> page(HttpServletRequest request, Authentication authentication,
			@RequestPart("editormd-image-file") MultipartFile file) {
		User user = (User) authentication.getPrincipal();
		try {
			SysFile sysFile = iSysFileService.upload(file.getOriginalFilename(), file.getBytes(), entity -> {
				entity.setCreatorUserId(user.getUserId());
				entity.setCreatorNickname(user.getNickname());
			});
			String uri = myConfigProperties.getCtx() + "/sys/file/download/" + sysFile.getFileId() + "?inline";
			return MapUtil.builder().put("success", 1).put("message", "成功").put("url", uri).build();
		} catch (Exception e) {
			e.printStackTrace();
			return MapUtil.builder().put("success", 0).put("message", "出错了").put("url", null).build();
		}
	}
}