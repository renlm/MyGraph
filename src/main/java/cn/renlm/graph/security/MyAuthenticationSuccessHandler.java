package cn.renlm.graph.security;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.renlm.graph.amqp.AmqpUtil;
import cn.renlm.graph.amqp.LoginLogQueue;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysLoginLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录成功处理
 * 
 * @author Renlm
 *
 */
@Component
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		this.sysLoginLog(request, authentication);
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
