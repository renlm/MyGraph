package cn.renlm.mygraph.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.util.AESUtil;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;

/**
 * 登录认证检查
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
public class UsernamePasswordAuthenticationProvider extends DaoAuthenticationProvider {

	private final CaptchaService captchaService;

	public UsernamePasswordAuthenticationProvider(UserService userService, CaptchaService captchaService) {
		this.setUserDetailsService(userService);
		this.captchaService = captchaService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object details = authentication.getDetails();
		if (details instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) details;
			CaptchaVO data = new CaptchaVO();
			data.setCaptchaVerification(webAuthenticationDetails.captchaVerification);
			try {
				ResponseModel verification = captchaService.verification(data);
				if (BooleanUtil.isFalse(verification.isSuccess())) {
					throw new SessionAuthenticationException("验证失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SessionAuthenticationException("验证失败");
			}
		}
		Authentication authen = super.authenticate(authentication);
		return authen;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		Object details = authentication.getDetails();
		if (details instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) details;
			try {
				String credentials = authentication.getCredentials().toString();
				String decryptCredentials = AESUtil.aesDecrypt(credentials, webAuthenticationDetails.secretKey);
				ReflectUtil.setFieldValue(authentication, "credentials", decryptCredentials);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BadCredentialsException("用户名或密码错误");
			}
		}
		super.additionalAuthenticationChecks(userDetails, authentication);
	}

}
