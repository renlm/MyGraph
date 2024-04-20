package cn.renlm.mygraph.amqp;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.convert.Convert;
import cn.renlm.mygraph.ws.WsMessage;
import cn.renlm.mygraph.ws.WsMessage.WsType;
import cn.renlm.mygraph.ws.WsUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 广播队列
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration
public class WsTopicQueue {

	private static final String KEY = "WsTopic";

	public static final String EXCHANGE = KEY + AmqpUtil.Exchange;

	public static final String QUEUE = KEY + AmqpUtil.Queue;

	public static final String ROUTINGKEY = QUEUE + AmqpUtil.RoutingKey;

	/**
	 * 监听广播队列
	 * 
	 * @param message
	 */
	@RabbitListener(queues = "#{" + QUEUE + ".name}")
	public void receiveMessage(WsMessage<Object> message) {
		WsType type = message.getType();
		// 上线 | 离线
		if (WsType.online.equals(type) || WsType.offline.equals(type)) {
			String userId = Convert.toStr(message.getData());
			long userConnections = WsUtil.getUserConnections(userId);
			log.info("=== {}，{}行为，该用户当前连接数：{}", userId, type, userConnections);
			message.setData(WsUtil.getOnlineUserNumber());
			WsUtil.topic(message);
		}
	}

	/**
	 * 声明交换机
	 * 
	 * @return
	 */
	@Bean(name = EXCHANGE)
	public DirectExchange exchange() {
		return ExchangeBuilder.directExchange(EXCHANGE).durable(true).build();
	}

	/**
	 * 声明队列
	 * 
	 * @return
	 */
	@Bean(name = QUEUE)
	public org.springframework.amqp.core.Queue queue() {
		return new AnonymousQueue();
	}

	/**
	 * 绑定队列到交换机
	 * 
	 * @param exchange
	 * @param queue
	 * @return
	 */
	@Bean(name = ROUTINGKEY)
	public Binding binding(@Qualifier(EXCHANGE) DirectExchange exchange,
			@Qualifier(QUEUE) org.springframework.amqp.core.Queue queue) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY);
	}
}