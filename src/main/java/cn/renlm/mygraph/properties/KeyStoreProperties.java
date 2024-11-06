package cn.renlm.mygraph.properties;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import com.nimbusds.jose.jwk.RSAKey;

import cn.hutool.crypto.asymmetric.RSA;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * 秘钥信息
 * keytool -genkeypair -alias alias -keyalg RSA -dname "C=CN" -keypass keypass -keystore keyStore.jks -storepass storepass
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Configuration
@ConfigurationProperties("encrypt.key-store")
public class KeyStoreProperties {

	private Resource location;

	private String alias;

	private String password;

	public RSAKey getRSAKey() {
		KeyStoreKeyFactory factory = new KeyStoreKeyFactory(location, password.toCharArray());
		KeyPair keyPair = factory.getKeyPair(alias);
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
