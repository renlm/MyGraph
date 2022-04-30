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
	 * 会话存储时长（秒），30分钟
	 */
	public static final int MAX_INACTIVE_INTERVAL_SECONDS = MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;

	/**
	 * 验证码-会话存储键
	 */
	public static final String CAPTCHA_SESSION_KEY = "CAPTCHA_SESSION_KEY";

	/**
	 * 验证码-参数键
	 */
	public static final String CAPTCHA_PARAM_NAME = "captcha";

	/**
	 * 用户名验证提示
	 */
	public final static String username_msg = "账号限定3~20个字符，只能包含字母、数字、下划线或横线";

	/**
	 * 用户名验证正则
	 */
	public final static String username_reg = "^[A-Z-a-z_\\d]{3,20}$";

	/**
	 * 密码验证提示
	 */
	public static final String password_msg = "密码限定6~20个字符，大小写字母、数字及特殊字符至少各一个";

	/**
	 * 密码验证正则
	 */
	public static final String password_reg = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)[\\s\\S]{6,20}";

	/**
	 * 系统常量
	 */
	public enum Sys {

		cfgSystemName("系统名称"), cfgSystemVersion("系统版本"), cfgSystemVersionPublishDate("发布日期"), cfgSiteBeian("网站备案号"),
		cfgEnableRegistration("是否启用注册账号");

		@Getter
		private final String text;

		private Sys(String text) {
			this.text = text;
		}
	}
}