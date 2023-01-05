package cn.renlm.graph.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

import com.baomidou.mybatisplus.core.toolkit.AES;

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
	 * 获取AES加密串
	 * 
	 * @param request
	 * @return
	 */
	public static final String getAesKey(final HttpServletRequest request) {
		HttpSession session = request.getSession();
		String aesKey = (String) session.getAttribute(AESKey);
		if (StrUtil.isBlank(aesKey)) {
			aesKey = AES.generateRandomKey();
			session.setAttribute(AESKey, aesKey);
		}
		return aesKey;
	}

	/**
	 * 删除会话信息
	 * 
	 * @param ticket
	 */
	public static final void deleteSession(String ticket) {
		String sessionId = Base64.decodeStr(ticket);
		if (StrUtil.isNotBlank(sessionId)) {
			RedisIndexedSessionRepository sessionRepository = SpringUtil.getBean(RedisIndexedSessionRepository.class);
			sessionRepository.deleteById(sessionId);
		}
	}

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