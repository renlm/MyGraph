package cn.renlm.graph.controller.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.mkopylec.charon.configuration.CharonConfigurer;

import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;

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
	private CharonConfigurer charonConfigurer;

	@Autowired
	private IGatewayProxyConfigService iGatewayProxyConfigService;

	/**
	 * 配置页
	 * 
	 * @return
	 */
	@GetMapping
	public String index() {
		iGatewayProxyConfigService.loadCofig(charonConfigurer);
		return "gateway/proxyConfig";
	}
}