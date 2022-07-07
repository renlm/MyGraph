package cn.renlm.graph.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import cn.renlm.graph.common.ConstVal;
import cn.renlm.graph.util.SessionUtil;
import lombok.Getter;

/**
 * 登录信息封装
 * 
 * @author Renlm
 *
 */
public class MyWebAuthenticationDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = 1L;

	@Getter
	private final String aesKey;

	@Getter
	private final String kaptcha;

	@Getter
	private final String captcha;

	public MyWebAuthenticationDetails(HttpServletRequest request) {
		super(request);
		HttpSession httpSession = request.getSession();
		this.aesKey = (String) httpSession.getAttribute(SessionUtil.AESKey);
		this.kaptcha = (String) httpSession.getAttribute(ConstVal.CAPTCHA_SESSION_KEY);
		this.captcha = request.getParameter(ConstVal.CAPTCHA_PARAM_NAME);
		httpSession.removeAttribute(ConstVal.CAPTCHA_SESSION_KEY);
	}
}