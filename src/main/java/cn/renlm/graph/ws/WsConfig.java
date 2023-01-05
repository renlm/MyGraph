package cn.renlm.graph.ws;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.properties.MyConfigProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 配置
 * 
 * @author Renlm
 *
 */
@Slf4j
@Configuration
@EnableWebSocket
public class WsConfig implements WebSocketConfigurer {

	@Autowired
	private MyConfigProperties myConfigProperties;

	@Autowired
	private WsHandler wsHandler;

	/**
	 * 在线状态心跳监测
	 */
	@Scheduled(cron = WsUtil.cron)
	public void heartbeat() {
		WsUtil.userOnlineStatusHeartbeat();
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		String wsAllowedOrigins = myConfigProperties.getWsAllowedOrigins();
		registry.addHandler(wsHandler, "ws/*")
				.setAllowedOrigins(Convert.toStrArray(StrUtil.splitTrim(wsAllowedOrigins, StrUtil.COMMA)))
				.addInterceptors(new HandshakeInterceptor() {
					@Override
					public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
							WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
						String path = request.getURI().getPath();
						log.debug("[ WebSocket ] 握手beforeHandshake：{}", path);
						// WsKey规则，Base64.encodeURI('token@timestamp')
						String wsKey = StrUtil.subAfter(path, StrUtil.SLASH, true);
						if (StrUtil.isBlank(wsKey) || !Base64.isBase64(wsKey)) {
							return false;
						}
						String decodeStr = Base64.decodeStr(wsKey);
						if (StrUtil.isBlank(decodeStr)) {
							return false;
						}
						String[] decodes = decodeStr.split(StrUtil.AT);
						if (ArrayUtil.isEmpty(decodes) || ArrayUtil.length(decodes) != 2) {
							return false;
						}
						String token = decodes[0];
						Long timestamp = Convert.toLong(decodes[1]);
						attributes.put(WsUtil.WsKey, wsKey);
						attributes.put(WsUtil.TokenKey, token);
						attributes.put(WsUtil.TimestampKey, timestamp);
						return WsUtil.validHandshake(token, timestamp);
					}

					@Override
					public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
							WebSocketHandler wsHandler, Exception exception) {
						String path = request.getURI().getPath();
						log.debug("[ WebSocket ] 握手afterHandshake：{}", path);
						if (ObjectUtil.isNotNull(exception)) {
							exception.printStackTrace();
						}
					}
				});
	}
}