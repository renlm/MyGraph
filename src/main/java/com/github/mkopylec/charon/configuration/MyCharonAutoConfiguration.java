package com.github.mkopylec.charon.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.mkopylec.charon.forwarding.MyReverseProxyFilter;
import com.github.mkopylec.charon.forwarding.ReverseProxyFilter;

import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import cn.renlm.graph.util.MyConfigProperties;

/**
 * 网关代理配置
 * 
 * @author Renlm
 *
 */
@Configuration
@AutoConfigureBefore(CharonAutoConfiguration.class)
public class MyCharonAutoConfiguration {

	@Bean
	@Primary
	ReverseProxyFilter reverseProxyFilter(MyConfigProperties myConfigProperties, ServerProperties serverProperties,
			IGatewayProxyConfigService iGatewayProxyConfigService) {
		CharonConfigurer configurer = GatewayUtil.configurers(myConfigProperties, serverProperties,
				iGatewayProxyConfigService);
		CharonConfiguration configuration = configurer.configure();
		return new MyReverseProxyFilter(configuration.getFilterOrder(),
				configuration.getRequestMappingConfigurations());
	}
}