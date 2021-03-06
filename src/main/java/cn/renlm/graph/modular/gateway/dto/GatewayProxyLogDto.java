package cn.renlm.graph.modular.gateway.dto;

import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 网关代理日志
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GatewayProxyLogDto extends GatewayProxyLog {

	private static final long serialVersionUID = 1L;

	/**
	 * 网关代理配置Uuid
	 */
	private String proxyConfigUuid;

	/**
	 * 搜索关键字
	 */
	private String keywords;

	/**
	 * 排序字段
	 */
	private String sort;

	/**
	 * 排序方式
	 */
	private String order;

}