package cn.renlm.graph;

import java.util.Scanner;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置加密
 * 
 * @author Renlm
 *
 */
@Slf4j
public class JasyptTest {

	final JasyptEncryptorConfigurationProperties properties = new JasyptEncryptorConfigurationProperties();

	@Test
	public void test() {
		String str;
		String pwd;
		@Cleanup
		Scanner sc = new Scanner(System.in);
		System.out.println("输入：");
		str = sc.next();
		System.out.println("密码：");
		pwd = sc.next();
		PooledPBEStringEncryptor encryptor = this.encryptor(pwd);
		String encrypted = encryptor.encrypt(str);
		String decrypted = encryptor.decrypt(encrypted);
		log.info("加密字符：{}", encrypted);
		log.info("解密字符：{}", decrypted);
	}

	PooledPBEStringEncryptor encryptor(String password) {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(password);
		config.setAlgorithm(properties.getAlgorithm());
		config.setKeyObtentionIterations(properties.getKeyObtentionIterations());
		config.setPoolSize(properties.getPoolSize());
		config.setProviderName(properties.getProviderName());
		config.setSaltGeneratorClassName(properties.getSaltGeneratorClassname());
		config.setIvGeneratorClassName(properties.getIvGeneratorClassname());
		config.setStringOutputType(properties.getStringOutputType());
		encryptor.setConfig(config);
		return encryptor;
	}

}
