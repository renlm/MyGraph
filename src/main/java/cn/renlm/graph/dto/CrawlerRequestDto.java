package cn.renlm.graph.dto;

import java.io.Serializable;

import lombok.Data;
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
public class CrawlerRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 强制更新
	 */
	private Boolean forceUpdate;

	/**
	 * 站点代码
	 */
	private String siteCode;

	/**
	 * 请求列表
	 */
	private Request[] requests;

}
