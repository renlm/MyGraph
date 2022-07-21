package cn.renlm.graph.common;

import org.springframework.session.MapSession;

import lombok.Getter;
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
	 * 会话存储时长（秒），十二小时
	 */
	public static final int MAX_INACTIVE_INTERVAL_SECONDS = MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS * 2 * 12;

	/**
	 * 验证码-会话存储键
	 */
	public static final String CAPTCHA_SESSION_KEY = "CAPTCHA_SESSION_KEY";

	/**
	 * 验证码-参数键
	 */
	public static final String CAPTCHA_PARAM_NAME = "captcha";

	/**
	 * 用户名-参数名
	 */
	public final static String USERNAME_PARAM_NAME = "username";

	/**
	 * 用户名验证正则
	 */
	public final static String username_reg = "^[A-Z-a-z_\\d]{3,20}$";

	/**
	 * 用户名验证提示
	 */
	public final static String username_msg = "账号限定3~20个字符，只能包含字母、数字、下划线或横线";

	/**
	 * 密码-参数名
	 */
	public static final String PASSWORD_PARAM_NAME = "password";

	/**
	 * 密码验证正则
	 */
	public static final String password_reg = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)[\\s\\S]{6,20}";

	/**
	 * 密码验证提示
	 */
	public static final String password_msg = "密码限定6~20个字符，大小写字母、数字及特殊字符至少各一个";

	/**
	 * Chrome 驱动配置
	 */
	public static final String chromeSetting = "config/chrome.setting";

	/**
	 * 系统常量
	 */
	public enum Sys {

		cfgSystemName("系统名称"), cfgSystemVersion("系统版本"), cfgSystemVersionPublishDate("发布日期"), cfgSiteBeian("网站备案号");

		@Getter
		private final String text;

		private Sys(String text) {
			this.text = text;
		}
	}
}