package cn.renlm.graph.ws;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.amqp.WsTopicQueueConfig;
import cn.renlm.graph.dto.UserDto;
import cn.renlm.graph.ws.WsMessage.WsType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 工具
 * 
 * @author Renlm
 *
 */
@Slf4j
@UtilityClass
public class WsUtil {

	/**
	 * 在线状态
	 */
	public static final String OnlineStatusKey = WsUtil.class.getName();
	public static final String OnlineStatusCron = "*/60 * * * * ?";
	public static final long OnlineStatusValidityMillis = 1000 * 60 * 5;

	/**
	 * 传递参数Key
	 */
	public static final String WsKey = "WsKey";
	public static final String TokenKey = "Token";
	public static final String TimestampKey = "Timestamp";

	/**
	 * 会话池
	 */
	private static final ConcurrentHashMap<String, UserDto> WS_USER_REL = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, WebSocketSession> WS_SESSION_POOL = new ConcurrentHashMap<>();

	/**
	 * 握手校验
	 * 
	 * @param token
	 * @param timestamp
	 * @return
	 */
	public static final boolean validHandshake(String token, Long timestamp) {
		UserDto user = getUserInfo(token, timestamp);
		return ObjectUtil.isNotEmpty(user) && StrUtil.isNotBlank(user.getUserId());
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
		String userId = user.getUserId();

		// 维护在线状态
		RedisTemplate<String, String> redisTemplate = getRedisTemplate();
		ZSetOperations<String, String> zops = redisTemplate.opsForZSet();
		Long expTime = DateUtil.current() + OnlineStatusValidityMillis;
		zops.add(OnlineStatusKey, userId, expTime);
		zops.add(userId, wsKey, expTime);

		// 广播上线状态
		AmqpTemplate amqpTemplate = SpringUtil.getBean(AmqpTemplate.class);
		WsMessage<String> wsMessage = WsMessage.build(WsType.online, userId);
		amqpTemplate.convertAndSend(WsTopicQueueConfig.EXCHANGE, WsTopicQueueConfig.ROUTINGKEY, wsMessage);

		WS_USER_REL.put(wsKey, user);
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
		UserDto user = getUserInfo(session);

		// 维护在线状态
		if (ObjectUtil.isNotEmpty(user)) {
			String userId = user.getUserId();
			RedisTemplate<String, String> redisTemplate = getRedisTemplate();
			ZSetOperations<String, String> zops = redisTemplate.opsForZSet();
			zops.remove(userId, wsKey);
			long userConnections = WsUtil.getUserConnections(userId);
			if (userConnections == 0) {
				zops.remove(OnlineStatusKey, userId);
			}

			// 广播离线状态
			AmqpTemplate amqpTemplate = SpringUtil.getBean(AmqpTemplate.class);
			WsMessage<String> wsMessage = WsMessage.build(WsType.offline, userId);
			amqpTemplate.convertAndSend(WsTopicQueueConfig.EXCHANGE, WsTopicQueueConfig.ROUTINGKEY, wsMessage);
		}

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
		final List<String> wsKeys = CollUtil.newArrayList(WS_SESSION_POOL.keys());
		for (String wsKey : wsKeys) {
			WebSocketSession session = WS_SESSION_POOL.get(wsKey);
			if (ObjectUtil.isNotEmpty(session)) {
				try {
					session.sendMessage(textMessage);
				} catch (IOException e) {
					e.printStackTrace();
					removeSession(session);
				}
			}
		}
	}

	/**
	 * 在线状态心跳监测
	 */
	public static final void userOnlineStatusHeartbeat() {
		log.info("=== 在线状态心跳监测，连接数：" + WS_USER_REL.size());
		RedisTemplate<String, String> redisTemplate = getRedisTemplate();
		ZSetOperations<String, String> zops = redisTemplate.opsForZSet();
		for (Map.Entry<String, UserDto> entry : WS_USER_REL.entrySet()) {
			String wsKey = entry.getKey();
			UserDto user = entry.getValue();
			String userId = user.getUserId();
			Long expTime = DateUtil.current() + OnlineStatusValidityMillis;
			WebSocketSession session = WS_SESSION_POOL.get(wsKey);
			if (ObjectUtil.isNotEmpty(session)) {
				zops.add(OnlineStatusKey, userId, expTime);
				zops.add(userId, wsKey, expTime);
			}
		}
	}

	/**
	 * 获取当前在线用户数
	 * 
	 * @return
	 */
	public static final long getOnlineUserNumber() {
		RedisTemplate<String, String> redisTemplate = getRedisTemplate();
		ZSetOperations<String, String> zops = redisTemplate.opsForZSet();
		Long min = DateUtil.current();
		Long max = min + OnlineStatusValidityMillis;
		return ObjectUtil.defaultIfNull(zops.count(OnlineStatusKey, min, max), 0L);
	}

	/**
	 * 获取用户连接数
	 * 
	 * @param userId
	 * @return
	 */
	public static final long getUserConnections(String userId) {
		RedisTemplate<String, String> redisTemplate = getRedisTemplate();
		ZSetOperations<String, String> zops = redisTemplate.opsForZSet();
		Long min = DateUtil.current();
		Long max = min + OnlineStatusValidityMillis;
		return ObjectUtil.defaultIfNull(zops.count(userId, min, max), 0L);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param session
	 * @return
	 */
	private static final UserDto getUserInfo(WebSocketSession session) {
		String wsKey = Convert.toStr(session.getAttributes().get(WsKey));
		if (StrUtil.isBlank(wsKey) || !Base64.isBase64(wsKey)) {
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
		Long timestamp = Convert.toLong(decodes[1]);
		return getUserInfo(token, timestamp);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param token
	 * @param timestamp
	 * @return
	 */
	private static final UserDto getUserInfo(String token, Long timestamp) {
		if (StrUtil.isBlank(token) || timestamp == null) {
			return null;
		}
		RedisTemplate<String, UserDto> redisTemplate = getRedisTemplate();
		return redisTemplate.opsForValue().get(token);
	}

	/**
	 * 获取Redis操作工具
	 * 
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	private static final <K, V> RedisTemplate<K, V> getRedisTemplate() {
		return SpringUtil.getBean(StrUtil.lowerFirst(RedisTemplate.class.getSimpleName()));
	}
}