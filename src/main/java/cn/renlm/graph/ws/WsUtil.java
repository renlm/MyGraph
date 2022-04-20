package cn.renlm.graph.ws;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.WebSocketSession;

import cn.hutool.core.convert.Convert;
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
		return true;
	}

	/**
	 * 添加会话
	 * 
	 * @param session
	 * @return
	 */
	public static final WebSocketSession addSession(WebSocketSession session) {
		String wsKey = Convert.toStr(session.getAttributes().get(WsKey));
		WS_USER_REL.put(wsKey, null);
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
}