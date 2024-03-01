package cn.renlm.graph;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.extra.spring.EnableSpringUtil;
import cn.renlm.graph.properties.MyConfigProperties;

/**
 * 应用启动入口
 * 
 * @author Renlm
 *
 */
@EnableAsync
@EnableScheduling
@EnableSpringUtil
@SpringBootApplication
public class GraphApplication {

	public static void main(String[] args) {
		System.setProperty("spring.amqp.deserialization.trust.all", "true");
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
	public RSA rsa(MyConfigProperties myConfigProperties) {
		String privateKeyBase64 = myConfigProperties.getRsaPrivateKeyStr();
		String publicKeyBase64 = myConfigProperties.getRsaPublicKeyStr();
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
