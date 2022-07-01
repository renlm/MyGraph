package cn.renlm.graph.controller.gateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
		return "gateway/proxyConfig";
	}
}