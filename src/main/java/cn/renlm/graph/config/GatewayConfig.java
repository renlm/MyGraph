package cn.renlm.graph.config;

import static com.github.mkopylec.charon.configuration.CharonConfigurer.charonConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mkopylec.charon.configuration.CharonConfigurer;

import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;

/**
 * 网关代理配置
 * 
 * @author Renlm
 *
 */
@Configuration
public class GatewayConfig {

	/**
	 * 网关代理路径
	 */
	public static final String proxyPath = "/gw/";

	@Bean
	CharonConfigurer charonConfigurer(IGatewayProxyConfigService iGatewayProxyConfigService) {
		return iGatewayProxyConfigService.loadCofig(charonConfiguration());
	}
}