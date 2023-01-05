package cn.renlm.graph.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 自定义配置
 * 
 * @author Renlm
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "my.config")
public class MyConfigProperties {

	private String ctx;

	private String wsHost;

	private String wssHost;

	private String wsAllowedOrigins;

	/**
	 * 队列线程数（网关代理日志记录）
	 */
	private String proxyLogConcurrency;

}