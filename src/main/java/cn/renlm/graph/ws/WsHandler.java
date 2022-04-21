package cn.renlm.graph.ws;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.oshi.OshiInfo;
import cn.renlm.graph.oshi.OshiInfoUtil;
import cn.renlm.graph.ws.WsMessage.WsType;

/**
 * WebSocket 处理器
 * 
 * @author Renlm
 *
 */
@Component
public class WsHandler extends TextWebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		WsUtil.addSession(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		if (JSONUtil.isTypeJSONObject(payload)) {
			WsMessage<?> wsMessage = JSONUtil.toBean(payload, WsMessage.class);
			WsType type = wsMessage.getType();
			// 服务器信息
			if (WsType.oshi.equals(type)) {
				Map<String, OshiInfo> servers = OshiInfoUtil.servers();
				for (Map.Entry<String, OshiInfo> entry : servers.entrySet()) {
					String ip = entry.getKey();
					Set<OshiInfo> infos = OshiInfoUtil.get(ip);
					final String messageJson = JSONUtil.toJsonStr(WsMessage.build(WsType.oshi, MapUtil.of(ip, infos)));
					final TextMessage textMessage = new TextMessage(messageJson);
					session.sendMessage(textMessage);
				}
			}
			// 即时通讯
			else if (WsType.im.equals(type)) {

			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		WsUtil.removeSession(session);
	}
}