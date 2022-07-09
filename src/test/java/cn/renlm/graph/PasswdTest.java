package cn.renlm.graph;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cn.hutool.core.util.IdUtil;

/**
 * 密码
 * 
 * @author Renlm
 *
 */
public class PasswdTest {

	@Test
	public void test() {
		String encodePasswd = new BCryptPasswordEncoder().encode("123654");
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