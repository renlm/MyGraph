package cn.renlm.graph.modular.gateway.dto;

import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 网关代理配置
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GatewayProxyConfigDto extends GatewayProxyConfig {

	private static final long serialVersionUID = 1L;

	private String keywords;

}