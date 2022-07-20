package cn.renlm.graph.config;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
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

import com.github.mkopylec.charon.configuration.GatewayUtil;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

/**
 * WebMvc配置
 *
 * @author Renlm
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	/**
	 * 跨域过滤优先级最高
	 */
	public static final int corsFilterOrder = Ordered.HIGHEST_PRECEDENCE;

	@Autowired
	private ThymeleafProperties thymeleafProperties;
	
	@Autowired
	private LocaleChangeInterceptor localeChangeInterceptor;

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
		corsConfigurationSource.registerCorsConfiguration(WebSecurityConfig.GwAntMatcher, config);
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource) {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
				if (CorsUtils.isPreFlightRequest(request)) {
					return;
				} else {
					boolean doCorsFilter = false;
					String uri = request.getRequestURI();
					if (StrUtil.startWith(uri, GatewayUtil.proxyPath)) {

					}
					if (doCorsFilter) {
						super.doFilterInternal(request, response, filterChain);
					}
				}
			}
		});
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