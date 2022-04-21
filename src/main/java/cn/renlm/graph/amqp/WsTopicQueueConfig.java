package cn.renlm.graph.amqp;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.oshi.OshiInfo;
import cn.renlm.graph.ws.WsUtil;

/**
 * WebSocket广播队列
 * 
 * @author Renlm
 *
 */
@Component
@Configuration
public class WsTopicQueueConfig {

	private static final String key = "WsTopic";

	public static final String exchange = key + AmqpUtil.Exchange;

	public static final String queue = key + AmqpUtil.Queue;

	public static final String routingKey = queue + AmqpUtil.RoutingKey;

	/**
	 * 监听广播队列
	 * 
	 * @param info
	 */
	@RabbitListener(queues = "#{" + queue + ".name}")
	public void receive(OshiInfo info) {
		String message = JSONUtil.toJsonStr(MapUtil.of(info.getIp(), info));
		WsUtil.topic(new TextMessage(message));
	}

	/**
	 * 声明交换机
	 * 
	 * @return
	 */
	@Bean(name = exchange)
	public DirectExchange exchange() {
		return ExchangeBuilder.directExchange(exchange).durable(true).build();
	}

	/**
	 * 声明队列
	 * 
	 * @return
	 */
	@Bean(name = queue)
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
	@Bean(name = routingKey)
	public Binding binding(@Qualifier(exchange) DirectExchange exchange,
			@Qualifier(queue) org.springframework.amqp.core.Queue queue) {
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}
}