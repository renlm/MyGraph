package cn.renlm.graph;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码
 * 
 * @author Renlm
 *
 */
public class PasswdTest {

	@Test
	public void test() {
		String encodePasswd = new BCryptPasswordEncoder().encode("Aac^123654.");
		System.out.println(encodePasswd);
	}
}