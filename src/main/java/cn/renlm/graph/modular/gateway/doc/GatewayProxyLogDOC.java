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
	 * 应用标识
	 */
	private String accessKey;

	/**
	 * 请求地址
	 */
	private String url;

	/**
	 * 代理地址
	 */
	private String proxyUrl;

	/**
	 * 请求时间
	 */
	private Date requestTime;

	/**
	 * 响应时间
	 */
	private Date responseTime;

	/**
	 * 响应码
	 */
	private Integer statusCode;

	/**
	 * 耗时（毫秒）
	 */
	private Long takeTime;

	/**
	 * 服务端ip
	 */
	private String serverIp;

	/**
	 * 客户端ip
	 */
	private String clientIp;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 账号
	 */
	private String username;

	/**
	 * 昵称
	 */
	private String nickname;

}