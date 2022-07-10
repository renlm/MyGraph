package cn.renlm.graph.security;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import cn.renlm.graph.amqp.AmqpUtil;
import cn.renlm.graph.amqp.LoginLogQueue;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysLoginLog;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.SessionUtil;
import lombok.Cleanup;

/**
 * 登录成功处理
 * 
 * @author Renlm
 *
 */
@Component
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		SessionUtil.getAesKey(request);
		// 添加登录凭证
		String sessionId = request.getSession().getId();
		User principal = (User) authentication.getPrincipal();
		principal.setTicket(Base64.encodeUrlSafe(sessionId));
		principal.setPassword(StrUtil.EMPTY);
		getContext().setAuthentication(authentication);
		// 处理响应结果
		String contentType = request.getContentType();
		this.sysLoginLog(request, authentication);
		super.setAlwaysUseDefaultTargetUrl(false);
		if (StrUtil.equals(ContentType.JSON.getValue(), contentType)) {
			response.setContentType("application/json;charset=" + request.getCharacterEncoding());
			@Cleanup
			PrintWriter out = response.getWriter();
			Result<?> result = Result.success(authentication.getPrincipal());
			out.write(objectMapper.writeValueAsString(result));
			out.close();
		} else {
			Map<String, String> paramMap = ServletUtil.getParamMap(request);
			String callback = paramMap.get("callback");
			if (HttpUtil.isHttp(callback) || HttpUtil.isHttps(callback)) {
				getRedirectStrategy().sendRedirect(request, response, callback);
			} else {
				super.onAuthenticationSuccess(request, response, authentication);
			}
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
		sysLoginLog.setClientIp(ServletUtil.getClientIP(request));
		sysLoginLog.setLoginTime(new Date());
		AmqpUtil.createQueue(LoginLogQueue.EXCHANGE, LoginLogQueue.ROUTINGKEY, sysLoginLog);
	}
}