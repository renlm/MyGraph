package cn.renlm.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.hutool.extra.spring.EnableSpringUtil;

/**
 * 应用启动入口
 * 
 * @author Renlm
 *
 */
@EnableSpringUtil
@SpringBootApplication
public class VirtualPhoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualPhoneApplication.class, args);
	}
}