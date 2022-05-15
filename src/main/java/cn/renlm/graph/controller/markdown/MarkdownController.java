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
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldLibDto;
import cn.renlm.graph.modular.er.entity.ErFieldLib;
import cn.renlm.graph.modular.er.service.IErFieldLibService;
import cn.renlm.graph.response.Result;

/**
 * Markdown 文档
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/erFieldLib")
public class MarkdownController {

	@Autowired
	private IErFieldLibService iErFieldLibService;

	/**
	 * 弹窗（新增|编辑）
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@GetMapping("/dialog")
	public String dialog(ModelMap model, String uuid) {
		ErFieldLib erFieldLib = new ErFieldLib();
		erFieldLib.setAutoIncrement(false);
		erFieldLib.setIsNullable(true);
		if (StrUtil.isNotBlank(uuid)) {
			ErFieldLib entity = iErFieldLibService
					.getOne(Wrappers.<ErFieldLib>lambdaQuery().eq(ErFieldLib::getUuid, uuid));
			BeanUtil.copyProperties(entity, erFieldLib);
		}
		model.put("erFieldLib", erFieldLib);
		return "er/fieldLibDialog";
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
	public Result<?> ajaxSave(Authentication authentication, ErFieldLibDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iErFieldLibService.ajaxSave(user, form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}