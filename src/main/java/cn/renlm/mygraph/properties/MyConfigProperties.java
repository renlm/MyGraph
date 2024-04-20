package cn.renlm.mygraph.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 自定义配置
 * 
 * @author RenLiMing(任黎明)
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

	private Chrome chrome = new Chrome();

	@Data
	public static class Chrome {

		private String driverPath = "/usr/bin/chromedriver";

		private String sleepTime = "2500";

	}

}
