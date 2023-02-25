package cn.renlm.graph.dto;

import cn.renlm.graph.modular.crawler.entity.CrawlerRequest;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSite;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.Request;

/**
 * 简易爬虫 - 访问请求
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrawlerRequestDto extends CrawlerRequest {

	private static final long serialVersionUID = 1L;

	/**
	 * 强制更新
	 */
	private Boolean forceUpdate;

	/**
	 * 站点配置
	 */
	private CrawlerSite site;

	/**
	 * 请求列表
	 */
	private Request[] requests;

}
