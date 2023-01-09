package cn.renlm.graph.modular.oauth2.dto;

import cn.renlm.graph.modular.oauth2.entity.Oauth2RegisteredClient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Oauth2.0 注册客户端
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Oauth2RegisteredClientDto extends Oauth2RegisteredClient {

	private static final long serialVersionUID = 1L;

	/**
	 * 关键字
	 */
	private String keywords;

}