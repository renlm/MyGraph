package cn.renlm.graph.config;

import static cn.hutool.core.text.StrPool.COMMA;
import static cn.hutool.core.text.StrPool.SLASH;
import static com.github.mkopylec.charon.configuration.CharonConfigurer.charonConfiguration;
import static com.github.mkopylec.charon.configuration.RequestMappingConfigurer.requestMapping;
import static com.github.mkopylec.charon.forwarding.RestTemplateConfigurer.restTemplate;
import static com.github.mkopylec.charon.forwarding.TimeoutConfigurer.timeout;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RegexRequestPathRewriterConfigurer.regexRequestPathRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestHostHeaderRewriterConfigurer.requestHostHeaderRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestProtocolHeadersRewriterConfigurer.requestProtocolHeadersRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestProxyHeadersRewriterConfigurer.requestProxyHeadersRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestServerNameRewriterConfigurer.requestServerNameRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.ResponseProtocolHeadersRewriterConfigurer.responseProtocolHeadersRewriter;
import static java.time.Duration.ofSeconds;

import java.util.List;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.mkopylec.charon.configuration.CharonConfigurer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
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
	public static final String proxyPath = "/proxy/";

	/**
	 * 初始化
	 * 
	 * @param serverProperties
	 * @param iGatewayProxyConfigService
	 * @return
	 */
	@Bean
	CharonConfigurer charonConfigurer(ServerProperties serverProperties,
			IGatewayProxyConfigService iGatewayProxyConfigService) {
		return configurers(charonConfiguration(), serverProperties, iGatewayProxyConfigService);
	}

	/**
	 * 加载数据库配置
	 * 
	 * @param configurer
	 * @param serverProperties
	 * @param iGatewayProxyConfigService
	 * @return
	 */
	private static final CharonConfigurer configurers(CharonConfigurer configurer, ServerProperties serverProperties,
			IGatewayProxyConfigService iGatewayProxyConfigService) {
		String contextPath = serverProperties.getServlet().getContextPath();
		List<GatewayProxyConfig> configs = iGatewayProxyConfigService
				.list(Wrappers.<GatewayProxyConfig>lambdaQuery().func(wrapper -> {
					wrapper.eq(GatewayProxyConfig::getEnabled, true);
					wrapper.orderByAsc(GatewayProxyConfig::getId);
				}));
		configs.forEach(config -> {
			String path = config.getPath();
			while (StrUtil.startWith(path, SLASH)) {
				path = StrUtil.removePrefix(path, SLASH);
			}
			while (StrUtil.endWith(path, SLASH)) {
				path = StrUtil.removeSuffix(path, SLASH);
			}
			List<String> outgoingServers = StrUtil.splitTrim(config.getOutgoingServers(), COMMA);
			CollUtil.removeBlank(outgoingServers);
			if (StrUtil.isNotBlank(path) && CollUtil.isNotEmpty(outgoingServers)) {
				final StringBuffer pathRegex = new StringBuffer();
				if (StrUtil.isNotBlank(contextPath) && !StrUtil.equals(contextPath, SLASH)) {
					pathRegex.append(contextPath);
				}
				pathRegex.append(proxyPath + path + "/.*");
				final String incomingRequestPathRegex = "/" + path + "/(?<path>.*)";
				final String outgoingRequestPathTemplate = "/<path>";
				configurer.add(
						requestMapping(path)
							.pathRegex(pathRegex.toString())
							.set(requestServerNameRewriter().outgoingServers(outgoingServers))
							.set(restTemplate().set(timeout().connection(ofSeconds(config.getConnectionTimeout())).read(ofSeconds(config.getReadTimeout())).write(ofSeconds(config.getWriteTimeout()))))
							.set(regexRequestPathRewriter().paths(incomingRequestPathRegex, outgoingRequestPathTemplate))
							.set(requestHostHeaderRewriter())
							.set(requestProtocolHeadersRewriter())
							.set(requestProxyHeadersRewriter())
							.set(responseProtocolHeadersRewriter())
					);
			}
		});
		return configurer;
	}
}