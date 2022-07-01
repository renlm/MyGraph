package cn.renlm.graph.config;

import static cn.hutool.core.text.StrPool.COMMA;
import static cn.hutool.core.text.StrPool.SLASH;
import static com.github.mkopylec.charon.configuration.CharonConfigurer.charonConfiguration;
import static com.github.mkopylec.charon.configuration.RequestMappingConfigurer.requestMapping;
import static com.github.mkopylec.charon.forwarding.RestTemplateConfigurer.restTemplate;
import static com.github.mkopylec.charon.forwarding.TimeoutConfigurer.timeout;
import static com.github.mkopylec.charon.forwarding.Utils.copyHeaders;
import static com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType.LATENCY_METER;
import static com.github.mkopylec.charon.forwarding.interceptors.log.ForwardingLoggerConfigurer.forwardingLogger;
import static com.github.mkopylec.charon.forwarding.interceptors.log.LogLevel.DEBUG;
import static com.github.mkopylec.charon.forwarding.interceptors.log.LogLevel.ERROR;
import static com.github.mkopylec.charon.forwarding.interceptors.log.LogLevel.INFO;
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
import org.springframework.http.HttpHeaders;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.mkopylec.charon.configuration.CharonConfigurer;
import com.github.mkopylec.charon.forwarding.interceptors.HttpRequest;
import com.github.mkopylec.charon.forwarding.interceptors.HttpRequestExecution;
import com.github.mkopylec.charon.forwarding.interceptors.HttpResponse;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptor;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorConfigurer;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import lombok.AllArgsConstructor;

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
							.set(requestHostHeaderRewriter())
							.set(requestProtocolHeadersRewriter())
							.set(requestProxyHeadersRewriter())
							.set(responseProtocolHeadersRewriter())
							.set(new MyRequestForwardingInterceptorConfigurer(config))
							.set(requestServerNameRewriter()
									.outgoingServers(outgoingServers))
							.set(regexRequestPathRewriter()
									.paths(incomingRequestPathRegex, outgoingRequestPathTemplate))
							.set(restTemplate()
									.set(timeout()
											.connection(ofSeconds(config.getConnectionTimeout()))
											.read(ofSeconds(config.getReadTimeout()))
											.write(ofSeconds(config.getWriteTimeout()))))
							.set(forwardingLogger()
			                        .successLogLevel(DEBUG)
			                        .clientErrorLogLevel(INFO)
			                        .serverErrorLogLevel(ERROR)
			                        .unexpectedErrorLogLevel(ERROR))
					);
			}
		});
		return configurer;
	}
	
	static class MyRequestForwardingInterceptorConfigurer
			extends RequestForwardingInterceptorConfigurer<MyRequestForwardingInterceptor> {
		MyRequestForwardingInterceptorConfigurer(GatewayProxyConfig proxy) {
			super(new MyRequestForwardingInterceptor(proxy));
		}
	}

	@AllArgsConstructor
	static class MyRequestForwardingInterceptor implements RequestForwardingInterceptor {

		private final GatewayProxyConfig proxy;

		@Override
		public HttpResponse forward(HttpRequest request, HttpRequestExecution execution) {
			HttpHeaders rewrittenHeaders = copyHeaders(request.getHeaders());
			rewrittenHeaders.set("Access-Key", proxy.getAccessKey());
			rewrittenHeaders.set("Time-Stamp", String.valueOf(DateUtil.current()));
			request.setHeaders(rewrittenHeaders);
			HttpResponse response = execution.execute(request);
			return response;
		}

		@Override
		public RequestForwardingInterceptorType getType() {
			return LATENCY_METER;
		}
	}
}