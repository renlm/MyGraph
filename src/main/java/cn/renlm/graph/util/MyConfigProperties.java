package cn.renlm.graph.util;

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

	private String wsHost;

	private String wssHost;

	private String wsAllowedOrigins;

}