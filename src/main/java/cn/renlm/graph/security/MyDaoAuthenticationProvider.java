package cn.renlm.graph.security;

import javax.annotation.PostConstruct;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;

import com.baomidou.mybatisplus.core.toolkit.AES;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 身份认证
 * 
 * @author Renlm
 *
 */
public class MyDaoAuthenticationProvider extends DaoAuthenticationProvider {

	private MessageSource messageSource;

	public MyDaoAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder,
			MessageSource messageSource) {
		this.messageSource = messageSource;
		this.setPasswordEncoder(passwordEncoder);
		this.setUserDetailsService(userService);
	}

	@PostConstruct
	public void initProvider() {
		this.messages = new MessageSourceAccessor(messageSource);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		MyWebAuthenticationDetails details = (MyWebAuthenticationDetails) authentication.getDetails();
		final String kaptcha = details.getKaptcha();
		final String captcha = details.getCaptcha();
		if (StrUtil.isBlank(kaptcha) || !StrUtil.equalsIgnoreCase(kaptcha, captcha)) {
			throw new InvalidCookieException("验证码错误");
		}
		Authentication authen = super.authenticate(authentication);
		return authen;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		MyWebAuthenticationDetails details = (MyWebAuthenticationDetails) authentication.getDetails();
		String credentials = AES.decrypt(Convert.toStr(authentication.getCredentials()), details.getAesKey());
		ReflectUtil.setFieldValue(authentication, "credentials", credentials);
		super.additionalAuthenticationChecks(userDetails, authentication);
	}
}