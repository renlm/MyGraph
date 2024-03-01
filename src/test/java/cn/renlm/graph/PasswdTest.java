package cn.renlm.graph;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import cn.hutool.core.util.IdUtil;

/**
 * 密码
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class PasswdTest {

	@Test
	public void test() {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		String encodePasswd = passwordEncoder.encode("123654");
		System.out.println(encodePasswd);
	}

	@Test
	public void uuid() {
		System.out.println(IdUtil.simpleUUID().toUpperCase());
		System.out.println(IdUtil.simpleUUID().toUpperCase());
		System.out.println(IdUtil.simpleUUID().toUpperCase());
		System.out.println(IdUtil.simpleUUID().toUpperCase());
		System.out.println(IdUtil.simpleUUID().toUpperCase());
	}
}