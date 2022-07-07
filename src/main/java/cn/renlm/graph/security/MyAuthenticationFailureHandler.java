package cn.renlm.graph.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.renlm.graph.config.WebSecurityConfig;
import cn.renlm.graph.response.Result;
import lombok.Cleanup;

/**
 * 登录失败处理
 * 
 * @author Renlm
 *
 */
@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String contentType = request.getContentType();
		if (StrUtil.equals(ContentType.JSON.getValue(), contentType)) {
			response.setContentType("application/json;charset=" + request.getCharacterEncoding());
			@Cleanup
			PrintWriter out = response.getWriter();
			Result<?> result = Result.of(HttpStatus.BAD_REQUEST, exception.getMessage());
			out.write(objectMapper.writeValueAsString(result));
			out.close();
		} else {
			super.setDefaultFailureUrl(WebSecurityConfig.LoginPage);
			super.onAuthenticationFailure(request, response, exception);
		}
	}
}