package cn.renlm.mygraph.security;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.renlm.mygraph.amqp.AmqpUtil;
import cn.renlm.mygraph.amqp.LoginLogQueue;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.sys.entity.SysLoginLog;
import cn.renlm.mygraph.properties.MyConfigProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录成功处理
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final MyConfigProperties myConfigProperties;

	private RequestCache requestCache = new HttpSessionRequestCache();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		this.sysLoginLog(request, authentication);
		SavedRequest savedRequest = this.requestCache.getRequest(request, response);
		if (savedRequest != null) {
			String targetUrl = savedRequest.getRedirectUrl();
			log.info("targetUrl = {}", targetUrl);
			String ctx = myConfigProperties.getCtx();
			if (!StrUtil.startWith(targetUrl, ctx + "/oauth2/authorize")) {
				this.getRedirectStrategy().sendRedirect(request, response, ctx);
				return;
			}
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}

	/**
	 * 记录登录日志
	 * 
	 * @param request
	 * @param authentication
	 */
	private void sysLoginLog(HttpServletRequest request, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		SysLoginLog sysLoginLog = new SysLoginLog();
		sysLoginLog.setUserId(user.getUserId());
		sysLoginLog.setUsername(user.getUsername());
		sysLoginLog.setNickname(user.getNickname());
		sysLoginLog.setClientIp(JakartaServletUtil.getClientIP(request));
		sysLoginLog.setLoginTime(new Date());
		AmqpUtil.createQueue(LoginLogQueue.EXCHANGE, LoginLogQueue.ROUTINGKEY, sysLoginLog);
	}

}
