package com.github.mkopylec.charon.configuration;

import static cn.hutool.core.codec.Base64.encodeUrlSafe;
import static cn.hutool.core.text.CharSequenceUtil.EMPTY;
import static cn.hutool.core.text.StrPool.COMMA;
import static cn.hutool.core.text.StrPool.SLASH;
import static cn.hutool.core.util.ReUtil.getGroup1;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.renlm.graph.util.SessionUtil.getUserInfo;
import static com.github.mkopylec.charon.configuration.CharonConfigurer.charonConfiguration;
import static com.github.mkopylec.charon.configuration.RequestMappingConfigurer.requestMapping;
import static com.github.mkopylec.charon.forwarding.RestTemplateConfigurer.restTemplate;
import static com.github.mkopylec.charon.forwarding.TimeoutConfigurer.timeout;
import static com.github.mkopylec.charon.forwarding.Utils.copyHeaders;
import static com.github.mkopylec.charon.forwarding.interceptors.log.ForwardingLoggerConfigurer.forwardingLogger;
import static com.github.mkopylec.charon.forwarding.interceptors.log.LogLevel.DEBUG;
import static com.github.mkopylec.charon.forwarding.interceptors.log.LogLevel.ERROR;
import static com.github.mkopylec.charon.forwarding.interceptors.log.LogLevel.INFO;
import static com.github.mkopylec.charon.forwarding.interceptors.metrics.LatencyMeterConfigurer.latencyMeter;
import static com.github.mkopylec.charon.forwarding.interceptors.metrics.RateMeterConfigurer.rateMeter;
import static com.github.mkopylec.charon.forwarding.interceptors.resilience.RateLimiterConfigurer.rateLimiter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RegexRequestPathRewriterConfigurer.regexRequestPathRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestHostHeaderRewriterConfigurer.requestHostHeaderRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestProtocolHeadersRewriterConfigurer.requestProtocolHeadersRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestProxyHeadersRewriterConfigurer.requestProxyHeadersRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestServerNameRewriterConfigurer.requestServerNameRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.ResponseProtocolHeadersRewriterConfigurer.responseProtocolHeadersRewriter;
import static io.github.resilience4j.ratelimiter.RateLimiterConfig.custom;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofSeconds;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;
import static org.springframework.http.HttpHeaders.COOKIE;

import java.util.List;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpHeaders;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.mkopylec.charon.forwarding.ReverseProxyFilter;
import com.github.mkopylec.charon.forwarding.interceptors.HttpRequest;
import com.github.mkopylec.charon.forwarding.interceptors.HttpRequestExecution;
import com.github.mkopylec.charon.forwarding.interceptors.HttpResponse;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptor;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorConfigurer;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.renlm.graph.dto.UserBase;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * 网关代理配置
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class CharonUtil {

	/**
	 * 网关代理路径
	 */
	public static final String proxyPath = "/proxy/";

	/**
	 * 扩展代理请求头
	 */
	public static final String HEADER_AccessKey = "GW-AccessKey";
	public static final String HEADER_UserInfo = "GW-UserInfo";
	public static final String HEADER_TimeStamp = "GW-Timestamp";
	public static final String HEADER_Sha256Hex = "GW-Sha256Hex";

	/**
	 * 重载配置
	 */
	public static final void reload() {
		ServerProperties serverProperties = SpringUtil.getBean(ServerProperties.class);
		IGatewayProxyConfigService iGatewayProxyConfigService = SpringUtil.getBean(IGatewayProxyConfigService.class);
		// 配置
		CharonConfigurer charonConfigurer = configurers(serverProperties, iGatewayProxyConfigService);
		CharonConfiguration configuration = charonConfigurer.configure();
		int filterOrder = configuration.getFilterOrder();
		List<RequestMappingConfiguration> rmcs = configuration.getRequestMappingConfigurations();
		// 注册
		ReverseProxyFilter reverseProxyFilter = new ReverseProxyFilter(filterOrder, rmcs);
		String beanName = StrUtil.lowerFirst(ReverseProxyFilter.class.getSimpleName());
		SpringUtil.unregisterBean(beanName);
		SpringUtil.registerBean(beanName, reverseProxyFilter);
	}

	/**
	 * 加载数据库配置
	 * 
	 * @param serverProperties
	 * @param iGatewayProxyConfigService
	 * @return
	 */
	public static final CharonConfigurer configurers(ServerProperties serverProperties,
			IGatewayProxyConfigService iGatewayProxyConfigService) {
		CharonConfigurer charonConfigurer = charonConfiguration();
		charonConfigurer.filterOrder(HIGHEST_PRECEDENCE + 100);
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
				if (StrUtil.isNotBlank(contextPath)) {
					if (BooleanUtil.isFalse(StrUtil.equals(contextPath, SLASH))) {
						pathRegex.append(contextPath);
					}
				}
				pathRegex.append(proxyPath + path + "/.*");
				final String incomingRequestPathRegex = "/" + path + "/(?<path>.*)";
				final String outgoingRequestPathTemplate = "/<path>";
				charonConfigurer.add(
						requestMapping(path)
							.pathRegex(pathRegex.toString())
							.set(requestHostHeaderRewriter())
							.set(requestProtocolHeadersRewriter())
							.set(requestProxyHeadersRewriter())
							.set(responseProtocolHeadersRewriter())
							.set(new MyRequestForwardingInterceptorConfigurer(config))
							.set(regexRequestPathRewriter()
									.paths(incomingRequestPathRegex, outgoingRequestPathTemplate))
							.set(requestServerNameRewriter()
									.outgoingServers(outgoingServers))
							.set(restTemplate()
									.set(
										timeout()
											.connection(ofSeconds(config.getConnectionTimeout()))
											.read(ofSeconds(config.getReadTimeout()))
											.write(ofSeconds(config.getWriteTimeout()))))
							.set(rateLimiter()
									.configuration(
										custom()
											.timeoutDuration(ZERO)
											.limitRefreshPeriod(ofSeconds(1))
											.limitForPeriod(ObjectUtil.defaultIfNull(config.getLimitForSecond(), 10000)))
									.meterRegistry(new LoggingMeterRegistry()))
							.set(latencyMeter()
									.meterRegistry(new LoggingMeterRegistry()))
							.set(rateMeter()
									.meterRegistry(new LoggingMeterRegistry()))
							.set(forwardingLogger()
									.successLogLevel(DEBUG)
									.clientErrorLogLevel(INFO)
									.serverErrorLogLevel(ERROR)
									.unexpectedErrorLogLevel(ERROR)));
			}
		});
		return charonConfigurer;
	}

	/**
	 * 自定义请求配置
	 */
	static class MyRequestForwardingInterceptorConfigurer
			extends RequestForwardingInterceptorConfigurer<MyRequestForwardingInterceptor> {
		MyRequestForwardingInterceptorConfigurer(GatewayProxyConfig proxy) {
			super(new MyRequestForwardingInterceptor(proxy));
		}
	}

	/**
	 * 自定义请求拦截器
	 */
	@AllArgsConstructor
	static class MyRequestForwardingInterceptor implements RequestForwardingInterceptor {

		final GatewayProxyConfig proxy;

		final RequestForwardingInterceptorType TYPE = new RequestForwardingInterceptorType(LOWEST_PRECEDENCE - 100);

		@Override
		public HttpResponse forward(HttpRequest request, HttpRequestExecution execution) {
			String accessKey = proxy.getAccessKey();
			String secretKey = proxy.getSecretKey();
			HttpHeaders rewrittenHeaders = copyHeaders(request.getHeaders());
			UserBase user = getUserInfo(getGroup1("SESSION=(.*?)(;|$)", rewrittenHeaders.getFirst(COOKIE)));
			String userInfo = new StringBuffer(encodeUrlSafe(user == null ? EMPTY : toJsonStr(user))).toString();
			String timestamp = String.valueOf(DateUtil.current());
			rewrittenHeaders.set(HEADER_AccessKey, accessKey);
			rewrittenHeaders.set(HEADER_UserInfo, userInfo);
			rewrittenHeaders.set(HEADER_TimeStamp, timestamp);
			rewrittenHeaders.set(HEADER_Sha256Hex, DigestUtil.sha256Hex(secretKey + timestamp + userInfo));
			request.setHeaders(rewrittenHeaders);
			HttpResponse response = execution.execute(request);
			return response;
		}

		@Override
		public RequestForwardingInterceptorType getType() {
			return TYPE;
		}
	}
}