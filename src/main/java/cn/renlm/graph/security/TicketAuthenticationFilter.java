package cn.renlm.graph.security;

import static cn.hutool.core.text.CharSequenceUtil.EMPTY;
import static cn.renlm.graph.util.GatewayUtil.HEADER_Ticket;
import static io.netty.util.CharsetUtil.UTF_8;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.util.SessionUtil;

/**
 * 认证过滤器（Ticket）
 * 
 * @author Renlm
 *
 */
@Component
public class TicketAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication authentication = getContext().getAuthentication();
		String ticket = ServletUtil.getHeader(request, HEADER_Ticket, UTF_8);
		// 优先获取会话
		if ((authentication == null || !authentication.isAuthenticated()) && StrUtil.isNotBlank(ticket)) {
			User user = SessionUtil.getUserInfo(ticket);
			// 根据登录凭证初始会话
			if (ObjectUtil.isNotEmpty(user)) {
				Authentication token = new UsernamePasswordAuthenticationToken(user, EMPTY, user.getAuthorities());
				getContext().setAuthentication(token);
			}
		}
		filterChain.doFilter(request, response);
	}
}