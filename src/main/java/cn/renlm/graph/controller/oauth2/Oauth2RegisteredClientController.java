package cn.renlm.graph.controller.oauth2;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.common.Role;
import cn.renlm.graph.modular.oauth2.dto.Oauth2RegisteredClientDto;
import cn.renlm.graph.modular.oauth2.entity.Oauth2RegisteredClient;
import cn.renlm.graph.modular.oauth2.service.IOauth2RegisteredClientService;
import cn.renlm.plugins.MyResponse.Datagrid;
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
	@GetMapping
	@PreAuthorize(Role.AdminSpEL)
	public String index(ModelMap model) {
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
	@PreAuthorize(Role.AdminSpEL)
	public Datagrid<Oauth2RegisteredClient> ajaxPage(Page<Oauth2RegisteredClient> page,
			Oauth2RegisteredClientDto form) {
		Page<Oauth2RegisteredClient> data = iOauth2RegisteredClientService.findPage(page, form);
		return Datagrid.of(data);
	}

}
