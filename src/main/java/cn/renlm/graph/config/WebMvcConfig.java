package cn.renlm.graph.config;

import static cn.hutool.core.text.StrPool.SLASH;
import static cn.renlm.graph.config.WebSecurityConfig.GwAntMatcher;
import static com.github.mkopylec.charon.configuration.GatewayUtil.proxyCorsMap;
import static com.github.mkopylec.charon.configuration.GatewayUtil.proxyPath;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

/**
 * WebMvc配置
 *
 * @author Renlm
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	public static final int corsFilterOrder = HIGHEST_PRECEDENCE;

	@Autowired
	private ThymeleafProperties thymeleafProperties;
	
	@Autowired
	private LocaleChangeInterceptor localeChangeInterceptor;

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOriginPattern(CorsConfiguration.ALL);
		config.addAllowedHeader(CorsConfiguration.ALL);
		config.addAllowedMethod(CorsConfiguration.ALL);
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				String servletPath = request.getServletPath();
				if (StrUtil.startWith(servletPath, proxyPath)) {
					String str = StrUtil.removePrefix(servletPath, proxyPath);
					String path = StrUtil.subBefore(str, SLASH, false);
					return proxyCorsMap.get(path);
				} else {
					return super.getCorsConfiguration(request);
				}
			}
		};
		corsConfigurationSource.registerCorsConfiguration(GwAntMatcher, config);
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
		bean.setOrder(corsFilterOrder);
		return bean;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new Converter<String, String>() {
			@Override
			public String convert(String source) {
				return StrUtil.trimToNull(source);
			}
		});
		registry.addConverter(new Converter<String, Date>() {
			@Override
			public Date convert(String source) {
				return DateUtil.parse(source);
			}
		});
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		VersionResourceResolver resolver = new VersionResourceResolver();
		resolver.addVersionStrategy(new ContentVersionStrategy(), "/**");

		registry.addResourceHandler("/static/**")
				.addResourceLocations("classpath:/static/")
				.resourceChain(thymeleafProperties.isCache())
				.addResolver(resolver);

		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/")
				.resourceChain(thymeleafProperties.isCache())
				.addResolver(resolver)
				.addResolver(new WebJarsResourceResolver())
				.addResolver(new PathResourceResolver());
	}
}