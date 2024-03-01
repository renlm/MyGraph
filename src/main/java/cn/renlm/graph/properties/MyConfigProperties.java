package cn.renlm.graph.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 自定义配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "my.config")
public class MyConfigProperties {

	private String ctx;

	private String wsHost;

	private String wssHost;

	private String wsAllowedOrigins;

	/**
	 * RSA 私钥Hex或Base64表示
	 */
	private String rsaPrivateKeyStr;

	/**
	 * RSA 公钥Hex或Base64表示
	 */
	private String rsaPublicKeyStr;

}
