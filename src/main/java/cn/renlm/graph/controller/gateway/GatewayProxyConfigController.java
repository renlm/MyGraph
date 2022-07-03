package cn.renlm.graph.controller.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyConfigDto;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.GatewayUtil;

/**
 * 网关代理配置
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/gateway/proxyConfig")
public class GatewayProxyConfigController {

	@Autowired
	private IGatewayProxyConfigService iGatewayProxyConfigService;

	/**
	 * 配置页
	 * 
	 * @return
	 */
	@GetMapping
	@PreAuthorize(Role.AdminSpEL)
	public String index() {
		return "gateway/proxyConfig";
	}

	/**
	 * 分页列表
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	@PreAuthorize(Role.AdminSpEL)
	public Datagrid<GatewayProxyConfig> page(Authentication authentication, Page<GatewayProxyConfig> page,
			GatewayProxyConfigDto form) {
		User user = (User) authentication.getPrincipal();
		Page<GatewayProxyConfig> data = iGatewayProxyConfigService.findPage(page, user, form);
		return Datagrid.of(data);
	}

	/**
	 * 弹窗（新增|编辑）
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/dialog")
	@PreAuthorize(Role.AdminSpEL)
	public String dialog(ModelMap model, String uuid) {
		GatewayProxyConfig gatewayProxyConfig = new GatewayProxyConfig();
		gatewayProxyConfig.setEnabled(true);
		gatewayProxyConfig.setConnectionTimeout(1);
		gatewayProxyConfig.setReadTimeout(600);
		gatewayProxyConfig.setWriteTimeout(600);
		gatewayProxyConfig.setLimitForSecond(10000);
		if (StrUtil.isNotBlank(uuid)) {
			GatewayProxyConfig entity = iGatewayProxyConfigService
					.getOne(Wrappers.<GatewayProxyConfig>lambdaQuery().eq(GatewayProxyConfig::getUuid, uuid));
			BeanUtil.copyProperties(entity, gatewayProxyConfig);
		}
		model.put("gatewayProxyConfig", gatewayProxyConfig);
		return "gateway/dialog";
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
	@PreAuthorize(Role.AdminSpEL)
	public Result<GatewayProxyConfigDto> ajaxSave(Authentication authentication, GatewayProxyConfigDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			Result<GatewayProxyConfigDto> result = iGatewayProxyConfigService.ajaxSave(user, form);
			if (result.isSuccess()) {
				GatewayUtil.reload(result.getData().getUuid());
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}