package cn.renlm.graph.security;

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
import cn.renlm.graph.amqp.AmqpUtil;
import cn.renlm.graph.amqp.LoginLogQueue;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysLoginLog;
import cn.renlm.graph.properties.MyConfigProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 登录成功处理
 * 
 * @author Renlm
 *
 */
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
			if (StrUtil.startWith(targetUrl, myConfigProperties.getCtx())) {
				this.getRedirectStrategy().sendRedirect(request, response, myConfigProperties.getCtx());
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
