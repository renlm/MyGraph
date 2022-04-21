package cn.renlm.graph.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.renlm.graph.dto.UserDto;
import cn.renlm.graph.ws.WsUtil;

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
		UserDto user = (UserDto) authentication.getPrincipal();
		long timeout = DateUtil.between(new Date(), user.getExpiryTime(), DateUnit.SECOND);
		ServletUtil.addCookie(response, WsUtil.TokenKey, user.getToken(), Convert.toInt(timeout));
		super.setAlwaysUseDefaultTargetUrl(true);
		super.onAuthenticationSuccess(request, response, authentication);
	}
}