package cn.renlm.graph.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * 国际化配置
 * 
 * @author Renlm
 *
 */
@Configuration
public class I18nConfig {
	private final static int CacheSeconds 	= -1;
	private final static int CookieMaxAge 	= 3600;
	private final static String ParamName 	= "lang";
	private final static String CookieName 	= "localeCookie";
	private final static String[] Basenames = { "classpath:org/springframework/security/messages" };
	
	@Bean
	@Primary
	MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setCacheSeconds(CacheSeconds);
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
	    messageSource.setBasenames(Basenames);
		return messageSource;
	}
	
	@Bean
	@Primary
    LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName(CookieName);
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        localeResolver.setCookieMaxAge(CookieMaxAge);
        return localeResolver;
    }
	
	@Bean
	@Primary
    LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(ParamName);
        return interceptor;
    }
}