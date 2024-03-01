package cn.renlm.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import cn.hutool.extra.spring.EnableSpringUtil;

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
public class MyGraphApplication {

	public static void main(String[] args) {
		System.setProperty("spring.amqp.deserialization.trust.all", "true");
		SpringApplication springApplication = new SpringApplication(MyGraphApplication.class);
		springApplication.run(args);
	}

}
