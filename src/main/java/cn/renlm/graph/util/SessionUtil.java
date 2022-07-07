package cn.renlm.graph.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.dto.UserBase;
import lombok.experimental.UtilityClass;

/**
 * 会话工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class SessionUtil {

	public static final String AESKey = "aesKey";

	/**
	 * 获取基本用户信息
	 * 
	 * @param ticket
	 * @return
	 */
	public static final UserBase getBaseUser(String ticket) {
		User user = getUserInfo(ticket);
		if (user == null) {
			return null;
		}
		return UserBase.of(user, ticket);
	}

	/**
	 * 获取会话用户信息
	 * 
	 * @param ticket
	 * @return
	 */
	public static final User getUserInfo(String ticket) {
		String sessionId = Base64.decodeStr(ticket);
		if (StrUtil.isBlank(sessionId)) {
			return null;
		}
		RedisIndexedSessionRepository sessionRepository = SpringUtil.getBean(RedisIndexedSessionRepository.class);
		Session session = sessionRepository.findById(sessionId);
		if (session == null || session.isExpired()) {
			return null;
		}
		SecurityContext securityContext = session
				.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if (securityContext == null) {
			return null;
		}
		Authentication authentication = securityContext.getAuthentication();
		if (authentication == null) {
			return null;
		}
		User user = (User) authentication.getPrincipal();
		user.setTicket(ticket);
		return user;
	}
}