package cn.renlm.graph.websocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
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
import cn.renlm.graph.util.MyConfigProperties;
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
						log.info("[ WebSocket ] 握手beforeHandshake：" + path);
						// WsKey规则，Base64.encodeURI('token@timestamp')
						String wsKey = StrUtil.subAfter(path, StrUtil.SLASH, true);
						String[] decodes = Base64.decodeStr(wsKey).split(StrUtil.AT);
						if (ArrayUtil.isNotEmpty(decodes) && ArrayUtil.length(decodes) == 2) {
							String token = decodes[0];
							Long timestamp = Convert.toLong(decodes[1]);
							attributes.put(WsUtil.WsKey, wsKey);
							attributes.put(WsUtil.TokenKey, token);
							attributes.put(WsUtil.TimestampKey, timestamp);
							return WsUtil.validHandshake(token, timestamp);
						}
						return false;
					}

					@Override
					public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
							WebSocketHandler wsHandler, Exception exception) {
						String path = request.getURI().getPath();
						log.info("[ WebSocket ] 握手afterHandshake：" + path);
						if (ObjectUtil.isNotNull(exception)) {
							exception.printStackTrace();
						}
					}
				});
	}
}