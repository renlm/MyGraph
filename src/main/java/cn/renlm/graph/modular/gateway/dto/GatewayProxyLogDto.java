package cn.renlm.graph.modular.gateway.dto;

import cn.renlm.graph.modular.gateway.dmt.GatewayProxyLogDmt;
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
public class GatewayProxyLogDto extends GatewayProxyLogDmt {

	private static final long serialVersionUID = 1L;

	/**
	 * 搜索关键字
	 */
	private String keywords;

}