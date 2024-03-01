package cn.renlm.graph;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nimbusds.jose.jwk.RSAKey;

import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.extra.spring.EnableSpringUtil;
import cn.renlm.graph.properties.KeyStoreProperties;
import cn.renlm.graph.properties.MyConfigProperties;
import lombok.SneakyThrows;

/**
 * 应用启动入口
 * 
 * @author RenLiMing(任黎明)
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
	@SneakyThrows
	public RSA rsa(MyConfigProperties myConfigProperties, KeyStoreProperties keyStoreProperties) {
		RSAKey key = keyStoreProperties.getRSAKey();
		return new RSA(key.toRSAPrivateKey(), key.toRSAPublicKey());
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return passwordEncoder;
	}

}
