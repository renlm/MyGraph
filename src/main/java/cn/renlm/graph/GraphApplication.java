package cn.renlm.graph;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.extra.spring.EnableSpringUtil;

/**
 * 应用启动入口
 * 
 * @author Renlm
 *
 */
@EnableAsync
@EnableSpringUtil
@SpringBootApplication
public class GraphApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GraphApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(GraphApplication.class);
		springApplication.run(args);
	}

	@Bean
	public MessageSource messageSource() {
		Locale.setDefault(Locale.CHINA);
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.addBasenames(new String[] { "classpath:org/springframework/security/messages" });
		return messageSource;
	}

	@Bean
	public RSA rsa() {
		String privateKeyBase64 = ResourceUtil.readUtf8Str("config/pub.asc");
		String publicKeyBase64 = ResourceUtil.readUtf8Str("config/pub");
		return new RSA(privateKeyBase64, publicKeyBase64);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return passwordEncoder;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduling = new ThreadPoolTaskScheduler();
		scheduling.setPoolSize(64);
		scheduling.initialize();
		return scheduling;
	}

}
