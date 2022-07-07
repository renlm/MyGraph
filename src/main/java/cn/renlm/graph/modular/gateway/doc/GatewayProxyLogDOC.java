package cn.renlm.graph.modular.gateway.doc;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

/**
 * 网关代理日志
 * 
 * @author Renlm
 *
 */
@Data
@Document(indexName = "gateway_proxy_log#{@elasticsearchConfig.indexSuffix}")
public class GatewayProxyLogDOC implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@Id
	@Field(type = FieldType.Keyword)
	private Long id;

	/**
	 * 访问地址
	 */
	private String url;

	/**
	 * 请求时间
	 */
	private Date requestTime;

	/**
	 * 响应时间
	 */
	private Date responseTime;

}