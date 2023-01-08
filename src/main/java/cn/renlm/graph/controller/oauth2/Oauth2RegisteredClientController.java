package cn.renlm.graph.controller.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.oauth2.dto.Oauth2RegisteredClientDto;
import cn.renlm.graph.modular.oauth2.entity.Oauth2RegisteredClient;
import cn.renlm.graph.modular.oauth2.service.IOauth2RegisteredClientService;
import cn.renlm.plugins.MyResponse.Datagrid;
import cn.renlm.plugins.MyResponse.Result;
import lombok.RequiredArgsConstructor;

/**
 * Oauth2.0 注册客户端
 * 
 * @author Renlm
 *
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth2/registeredClient")
public class Oauth2RegisteredClientController {

	private final IOauth2RegisteredClientService iOauth2RegisteredClientService;

	/**
	 * 列表页
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String list(ModelMap model) {
		return "oauth2/registeredClient";
	}

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Datagrid<Oauth2RegisteredClient> ajaxPage(Page<Oauth2RegisteredClient> page,
			Oauth2RegisteredClientDto form) {
		Page<Oauth2RegisteredClient> data = iOauth2RegisteredClientService.findPage(page, form);
		return Datagrid.of(data);
	}

	/**
	 * 弹窗（新建|编辑）
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String id) {
		Oauth2RegisteredClient registeredClient = new Oauth2RegisteredClient();
		if (StrUtil.isNotBlank(id)) {
			Oauth2RegisteredClient entity = iOauth2RegisteredClientService.getById(id);
			BeanUtil.copyProperties(entity, registeredClient);
		}
		model.put("registeredClient", registeredClient);
		return "oauth2/registeredClientDialog";
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
	public Result<String> ajaxSave(Authentication authentication, Oauth2RegisteredClientDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iOauth2RegisteredClientService.ajaxSave(user, form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

}
