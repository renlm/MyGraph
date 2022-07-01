package cn.renlm.graph.controller.gateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.renlm.graph.config.GatewayConfig;

/**
 * 网关代理配置
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/gateway/proxyConfig")
public class GatewayProxyConfigController {

	/**
	 * 配置页
	 * 
	 * @return
	 */
	@GetMapping
	public String index() {
		GatewayConfig.reload();
		return "gateway/proxyConfig";
	}
}