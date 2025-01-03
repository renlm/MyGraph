package cn.renlm.mygraph.security;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.renlm.mygraph.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录认证信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class WebAuthenticationDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	String secretKey;

	String captchaVerification;

	public WebAuthenticationDetails() {
	}

	public WebAuthenticationDetails(HttpServletRequest request) {
		Map<String, String> paramMap = JakartaServletUtil.getParamMap(request);
		this.secretKey = SessionUtil.getAesKey(request);
		this.captchaVerification = paramMap.get("captchaVerification");
	}

}
