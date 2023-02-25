package cn.renlm.graph.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 爬虫配置
 * 
 * @author Renlm
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "crawler.config")
public class CrawlerConfigProperties {

	private String driverPath;

	private Integer sleepTime;

	private CrawlerProxy proxy;

	private Map<String, CrawlerSite> sites;

	public static final class CrawlerSite {

	}

	/**
	 * 代理配置
	 */
	@Data
	public static final class CrawlerProxy {

		private boolean enable;

		private String host;

		private int port;

		private String username;

		private String password;

	}

}
