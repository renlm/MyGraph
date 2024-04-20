package cn.renlm.mygraph;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

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

}
