package cn.renlm.graph.properties;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 爬虫配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "crawler.config")
public class CrawlerConfigProperties {

	private String driverPath;

	private Integer sleepTime;

	private CrawlerProxy proxy;

	private List<CrawlerSite> sites;

	/**
	 * 站点配置
	 */
	@Data
	public static final class CrawlerSite {

		/**
		 * 代码
		 */
		private String code;

		/**
		 * 名称
		 */
		private String name;

		/**
		 * 休眠时间（毫秒）
		 */
		private Integer sleepTime;

		/**
		 * 超时时间（毫秒）
		 */
		private Integer timeout;

		/**
		 * 主域名
		 */
		private String domain;

		/**
		 * UserAgent
		 */
		private String userAgent;

		/**
		 * 是否启用浏览器模式（默认否）
		 */
		private boolean enableSelenuim;

		/**
		 * 保存页面截图（浏览器模式下，默认否）
		 */
		private boolean screenshot;

		/**
		 * 是否保存网页内容（默认否）
		 */
		private boolean saveHtml;

		/**
		 * 是否启用定时任务（默认否）
		 */
		private boolean enableCron;

		/**
		 * 延迟秒数
		 */
		private int cronDelaySeconds;

		/**
		 * 是否清除Url参数（默认否）
		 */
		private boolean cleanParams;

		/**
		 * 无效参数（多个逗号分隔，从Url删除此参数，降低重复请求次数）
		 */
		private String invalidParamNames;

		/**
		 * Header
		 */
		private List<Map<String, String>> headers;

		/**
		 * Cookie（[域名：{ key: value }]）
		 */
		private List<Map<String, Map<String, String>>> cookies;

		/**
		 * 动态脚本
		 */
		private List<String> scripts;

		/**
		 * 站点入口
		 */
		private List<CrawlerSiteEndpoint> endpoints;

	}

	/**
	 * 站点入口
	 */
	@Data
	public static final class CrawlerSiteEndpoint {

		/**
		 * 入口链接
		 */
		private String startUrl;

		/**
		 * 匹配正则
		 */
		private String regex;

		/**
		 * 正则Group（默认0）
		 */
		private int regexGroup;

		/**
		 * 页面链接类型，0：种子，1：数据
		 */
		private Integer pageUrlType;

		/**
		 * 爬取深度，默认0（无限制）
		 */
		private int depth;

		/**
		 * 标记值
		 */
		private List<String> flag;

		/**
		 * 动态脚本
		 */
		private List<String> scripts;

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
