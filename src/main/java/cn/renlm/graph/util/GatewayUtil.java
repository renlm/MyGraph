package cn.renlm.graph.util;

import static cn.hutool.core.codec.Base64.encodeUrlSafe;
import static cn.hutool.core.text.CharSequenceUtil.EMPTY;
import static cn.hutool.core.text.StrPool.COMMA;
import static cn.hutool.core.text.StrPool.SLASH;
import static cn.hutool.core.util.ReUtil.getGroup1;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.renlm.graph.util.SessionUtil.getBaseUser;
import static com.github.mkopylec.charon.configuration.CharonConfigurer.charonConfiguration;
import static com.github.mkopylec.charon.configuration.RequestMappingConfigurer.requestMapping;
import static com.github.mkopylec.charon.forwarding.RequestForwardingException.requestForwardingError;
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
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RootPathResponseCookiesRewriterConfigurer.rootPathResponseCookiesRewriter;
import static io.github.resilience4j.ratelimiter.RateLimiterConfig.custom;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpHeaders.COOKIE;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.mkopylec.charon.configuration.CharonConfigurer;
import com.github.mkopylec.charon.forwarding.ReverseProxyFilter;
import com.github.mkopylec.charon.forwarding.interceptors.HttpRequest;
import com.github.mkopylec.charon.forwarding.interceptors.HttpRequestExecution;
import com.github.mkopylec.charon.forwarding.interceptors.HttpResponse;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptor;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorConfigurer;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.renlm.graph.dto.UserBase;
import cn.renlm.graph.modular.gateway.dmt.GatewayProxyLogDmt;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 网关工具
 * 
 * @author Renlm
 *
 */
@Slf4j
@UtilityClass
public class GatewayUtil {

	/**
	 * 配置对象
	 */
	private static final CharonConfigurer charonConfigurer = charonConfiguration();

	/**
	 * 网关代理路径
	 */
	public static final String proxyPath = "/proxy/";

	/**
	 * 扩展代理请求头
	 */
	public static final String HEADER_Ticket = "GW-Ticket";
	public static final String HEADER_AccessKey = "GW-AccessKey";
	public static final String HEADER_UserInfo = "GW-UserInfo";
	public static final String HEADER_TimeStamp = "GW-Timestamp";
	public static final String HEADER_Sha256Hex = "GW-Sha256Hex";

	/**
	 * 重载配置
	 * 
	 * @param uuids
	 */
	public static final void reload(String... uuids) {
		MyConfigProperties myConfigProperties = SpringUtil.getBean(MyConfigProperties.class);
		ServerProperties serverProperties = SpringUtil.getBean(ServerProperties.class);
		IGatewayProxyConfigService iGatewayProxyConfigService = SpringUtil.getBean(IGatewayProxyConfigService.class);
		ReverseProxyFilter reverseProxyFilter = SpringUtil.getBean(ReverseProxyFilter.class);
		configurers(myConfigProperties, serverProperties, iGatewayProxyConfigService, uuids);
		ReflectUtil.invoke(charonConfigurer, "configure");
		Object restTemplateProvider = ReflectUtil.getFieldValue(reverseProxyFilter, "restTemplateProvider");
		ConcurrentMap<?, ?> restTemplates = (ConcurrentMap<?, ?>) ReflectUtil.getFieldValue(restTemplateProvider, "restTemplates");
		restTemplates.clear();
	}

	/**
	 * 加载数据库配置
	 * 
	 * @param myConfigProperties
	 * @param serverProperties
	 * @param iGatewayProxyConfigService
	 * @param uuids
	 * @return
	 */
	public static final CharonConfigurer configurers(MyConfigProperties myConfigProperties,
			ServerProperties serverProperties, IGatewayProxyConfigService iGatewayProxyConfigService, String... uuids) {
		String contextPath = serverProperties.getServlet().getContextPath();
		List<GatewayProxyConfig> configs = iGatewayProxyConfigService
				.list(Wrappers.<GatewayProxyConfig>lambdaQuery().func(wrapper -> {
					if (ArrayUtil.isNotEmpty(uuids)) {
						wrapper.in(GatewayProxyConfig::getUuid, CollUtil.newArrayList(uuids));
					}
					wrapper.orderByAsc(GatewayProxyConfig::getId);
				}));
		configs.forEach(config -> {
			String path = config.getPath();
			List<String> outgoingServers = StrUtil.splitTrim(config.getOutgoingServers(), COMMA);
			CollUtil.removeBlank(outgoingServers);
			boolean disabled = !BooleanUtil.isTrue(config.getEnabled());
			if (disabled) {
				outgoingServers.clear();
				outgoingServers.add(myConfigProperties.getCtx());
			}
			if (StrUtil.isNotBlank(path) && CollUtil.isNotEmpty(outgoingServers)) {
				final String root = StrUtil.isNotBlank(contextPath) && BooleanUtil.isFalse(StrUtil.equals(contextPath, SLASH)) ? contextPath : EMPTY;
				final StringBuffer pathRegex = new StringBuffer(root);
				pathRegex.append(proxyPath + path + "/.*");
				final String incomingRequestPathRegex = root + proxyPath + path + "/(?<path>.*)";
				final String outgoingRequestPathTemplate = disabled ? (root + "/_<path>_") : "/<path>";
				charonConfigurer
					.add(requestMapping(path)
							.pathRegex(pathRegex.toString())
							.set(rootPathResponseCookiesRewriter())
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
									.unexpectedErrorLogLevel(ERROR)))
					;
				log.info("==> 加载网关代理配置：{}", path);
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

		private final GatewayProxyConfig proxy;

		static final RequestForwardingInterceptorType TYPE = new RequestForwardingInterceptorType(-100);

		@Override
		public HttpResponse forward(HttpRequest request, HttpRequestExecution execution) {
			// <!- 代理日志
			final GatewayProxyLogDmt proxyLog = new GatewayProxyLogDmt();
			proxyLog.setAccessKey(proxy.getAccessKey());
			proxyLog.setUrl(request.getURI().toString());
			proxyLog.setHttpMethod(request.getMethod().toString());
			proxyLog.setRequestTime(new Date());
			proxyLog.setProxyPath(proxy.getPath());
			proxyLog.setProxyName(proxy.getName());
			proxyLog.setProxyOutgoingServers(proxy.getOutgoingServers());
			proxyLog.setProxyConnectionTimeout(proxy.getConnectionTimeout());
			proxyLog.setProxyReadTimeout(proxy.getReadTimeout());
			proxyLog.setProxyWriteTimeout(proxy.getWriteTimeout());
			proxyLog.setProxyLimitForSecond(proxy.getLimitForSecond());
			// -!> 代理日志
			try {
				String accessKey = proxy.getAccessKey();
				String secretKey = proxy.getSecretKey();
				HttpHeaders rewrittenHeaders = copyHeaders(request.getHeaders());
				UserBase user = getBaseUser(getGroup1("SESSION=(.*?)(;|$)", rewrittenHeaders.getFirst(COOKIE)));
				String userInfo = new StringBuffer(encodeUrlSafe(user == null ? EMPTY : toJsonStr(user))).toString();
				String timestamp = String.valueOf(DateUtil.current());
				rewrittenHeaders.set(HEADER_AccessKey, accessKey);
				rewrittenHeaders.set(HEADER_UserInfo, userInfo);
				rewrittenHeaders.set(HEADER_TimeStamp, timestamp);
				rewrittenHeaders.set(HEADER_Sha256Hex, DigestUtil.sha256Hex(secretKey + timestamp + userInfo));
				request.setHeaders(rewrittenHeaders);
				// <!- 代理日志
				proxyLog.setUserId(user == null ? null : user.getUserId());
				proxyLog.setUsername(user == null ? null : user.getUsername());
				proxyLog.setNickname(user == null ? null : user.getNickname());
				// -!> 代理日志
				HttpResponse response = execution.execute(request);
				// <!- 代理日志
				proxyLog.setProxyUrl(request.getURI().toString());
				proxyLog.setResponseTime(new Date());
				proxyLog.setStatusCode(response.getStatusCode().value());
				proxyLog.setStatusText(response.getStatusText());
				// -!> 代理日志
				return response;
			} catch (Exception e) {
				// <!- 代理日志
				proxyLog.setResponseTime(new Date());
				proxyLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				proxyLog.setErrorMessage(e.getMessage());
				// -!> 代理日志
				throw requestForwardingError("Error executing request: " + e.getMessage(), e);
			} finally {
				// <!- 代理日志
				proxyLog.setTakeTime(proxyLog.getRequestTime().getTime() - proxyLog.getResponseTime().getTime());
				SpringUtil.getBean(IGatewayProxyConfigService.class).recordLog(proxyLog);
				// -!> 代理日志
			}
		}

		@Override
		public RequestForwardingInterceptorType getType() {
			return TYPE;
		}
	}
}