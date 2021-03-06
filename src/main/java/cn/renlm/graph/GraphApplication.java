package cn.renlm.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.github.mkopylec.charon.configuration.GatewayConfig;

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
@ImportAutoConfiguration(GatewayConfig.class)
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
	public RSA rsa() {
		String privateKeyBase64 = ResourceUtil.readUtf8Str("config/pub.asc");
		String publicKeyBase64 = ResourceUtil.readUtf8Str("config/pub");
		return new RSA(privateKeyBase64, publicKeyBase64);
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduling = new ThreadPoolTaskScheduler();
		scheduling.setPoolSize(128);
		scheduling.initialize();
		return scheduling;
	}
}