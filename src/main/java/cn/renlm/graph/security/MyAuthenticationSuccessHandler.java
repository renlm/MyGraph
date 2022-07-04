package cn.renlm.graph.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.amqp.AmqpUtil;
import cn.renlm.graph.amqp.LoginLogQueue;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysLoginLog;
import cn.renlm.graph.response.Result;
import lombok.Cleanup;

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
		String contentType = request.getContentType();
		this.sysLoginLog(request, authentication);
		super.setAlwaysUseDefaultTargetUrl(false);
		if (StrUtil.equals(ContentType.JSON.getValue(), contentType)) {
			response.setContentType("application/javascript;charset=" + request.getCharacterEncoding());
			@Cleanup
			PrintWriter out = response.getWriter();
			out.write(JSONUtil.toJsonStr(Result.success(authentication.getPrincipal())));
		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
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
		sysLoginLog.setClientIp(ServletUtil.getClientIPByHeader(request, "X-Forwarded-For"));
		sysLoginLog.setLoginTime(new Date());
		AmqpUtil.createQueue(LoginLogQueue.EXCHANGE, LoginLogQueue.ROUTINGKEY, sysLoginLog);
	}
}