package cn.renlm.graph.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
import cn.renlm.graph.common.ConstVal;

/**
 * WebMvc配置
 *
 * @author Renlm
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = ConstVal.MAX_INACTIVE_INTERVAL_SECONDS)
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private ThymeleafProperties thymeleafProperties;
	
	@Autowired
	private LocaleChangeInterceptor localeChangeInterceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(GatewayConfig.proxyPath + "**")
			.allowCredentials(true)
			.allowedHeaders("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedOriginPatterns("*");
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