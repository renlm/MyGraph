package cn.renlm.graph.config;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mkopylec.charon.configuration.CharonConfigurer;

import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import cn.renlm.graph.util.GatewayUtil;
import cn.renlm.graph.util.MyConfigProperties;

/**
 * 网关代理配置
 * 
 * @author Renlm
 *
 */
@Configuration
public class GatewayConfig {

	@Bean
	CharonConfigurer charonConfigurer(MyConfigProperties myConfigProperties, ServerProperties serverProperties,
			IGatewayProxyConfigService iGatewayProxyConfigService) {
		return GatewayUtil.configurers(myConfigProperties, serverProperties, iGatewayProxyConfigService);
	}
}