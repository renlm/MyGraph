package cn.renlm.graph.properties;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.RSAKey;

import cn.hutool.crypto.asymmetric.RSA;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * 秘钥信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Getter
@Setter
@Component
@ConfigurationProperties("encrypt.key-store")
public class KeyStoreProperties {

	private Resource location;

	private String password;

	private String alias;

	private String secret;

	public RSAKey getRSAKey() {
		KeyStoreKeyFactory factory = new KeyStoreKeyFactory(location, secret.toCharArray());
		KeyPair keyPair = factory.getKeyPair(alias, password.toCharArray());
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(alias).build();
	}

	@Bean
	@SneakyThrows
	public RSA rsa() {
		RSAKey key = this.getRSAKey();
		return new RSA(key.toRSAPrivateKey(), key.toRSAPublicKey());
	}

}
