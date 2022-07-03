package cn.renlm.graph.config;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mkopylec.charon.configuration.CharonConfigurer;

import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import cn.renlm.graph.util.CharonUtil;

/**
 * 网关代理配置
 * 
 * @author Renlm
 *
 */
@Configuration
public class GatewayConfig {

	@Bean
	CharonConfigurer charonConfigurer(ServerProperties serverProperties,
			IGatewayProxyConfigService iGatewayProxyConfigService) {
		return CharonUtil.configurers(serverProperties, iGatewayProxyConfigService);
	}
}