package cn.renlm.graph.ws;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.dto.UserDto;
import lombok.experimental.UtilityClass;

/**
 * WebSocket 工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class WsUtil {

	/**
	 * 传递参数Key
	 */
	public static final String WsKey = "WsKey";
	public static final String TokenKey = "Token";
	public static final String TimestampKey = "Timestamp";

	/**
	 * 会话池
	 */
	private static final ConcurrentHashMap<String, String> WS_USER_REL = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, WebSocketSession> WS_SESSION_POOL = new ConcurrentHashMap<>();

	/**
	 * 握手校验
	 * 
	 * @param token
	 * @param timestamp
	 * @return
	 */
	public static final boolean validHandshake(String token, Long timestamp) {
		UserDto user = getUserInfo(token);
		return ObjectUtil.isNotEmpty(user);
	}

	/**
	 * 添加会话
	 * 
	 * @param session
	 * @return
	 */
	public static final WebSocketSession addSession(WebSocketSession session) {
		String wsKey = Convert.toStr(session.getAttributes().get(WsKey));
		UserDto user = getUserInfo(session);
		WS_USER_REL.put(wsKey, user.getUserId());
		return WS_SESSION_POOL.put(wsKey, session);
	}

	/**
	 * 删除会话
	 * 
	 * @param session
	 * @return
	 */
	public static final WebSocketSession removeSession(WebSocketSession session) {
		String wsKey = Convert.toStr(session.getAttributes().get(WsKey));
		WS_USER_REL.remove(wsKey);
		return WS_SESSION_POOL.remove(wsKey);
	}

	/**
	 * 广播消息
	 * 
	 * @param message
	 */
	public static final void topic(WsMessage<?> message) {
		if (ObjectUtil.isEmpty(message)) {
			return;
		}
		final String messageJson = JSONUtil.toJsonStr(message);
		final TextMessage textMessage = new TextMessage(messageJson);
		WS_SESSION_POOL.forEach((key, value) -> {
			try {
				value.sendMessage(textMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 获取用户信息
	 * 
	 * @param session
	 * @return
	 */
	private static final UserDto getUserInfo(WebSocketSession session) {
		String wsKey = Convert.toStr(session.getAttributes().get(WsKey));
		if (StrUtil.isBlank(wsKey)) {
			return null;
		}
		String decodeStr = Base64.decodeStr(wsKey);
		if (StrUtil.isBlank(decodeStr)) {
			return null;
		}
		String[] decodes = decodeStr.split(StrUtil.AT);
		if (ArrayUtil.isEmpty(decodes) || ArrayUtil.length(decodes) != 2) {
			return null;
		}
		String token = decodes[0];
		return getUserInfo(token);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param token
	 * @return
	 */
	private static final UserDto getUserInfo(String token) {
		RedisTemplate<String, UserDto> redisTemplate = SpringUtil
				.getBean(new TypeReference<RedisTemplate<String, UserDto>>() {
				});
		return redisTemplate.opsForValue().get(token);
	}
}