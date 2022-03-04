package cn.renlm.graph.common;

import lombok.experimental.UtilityClass;

/**
 * 常量池
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class ConstVal {

	/**
	 * 验证码-会话存储键
	 */
	public static final String CAPTCHA_SESSION_KEY = "CAPTCHA_SESSION_KEY";

	/**
	 * 验证码-参数键
	 */
	public static final String CAPTCHA_PARAM_NAME = "captcha";

	/**
	 * 密码验证提示
	 */
	public static final String password_msg = "密码限定6~20个字符，大小写字母、数字及特殊字符至少各一个";

	/**
	 * 密码验证正则
	 */
	public static final String password_reg = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)[\\s\\S]{6,20}";

}