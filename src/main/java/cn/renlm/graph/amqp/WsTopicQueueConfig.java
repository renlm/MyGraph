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

import cn.renlm.graph.oshi.OshiInfo;
import cn.renlm.graph.oshi.OshiInfoUtil;
import cn.renlm.graph.ws.WsMessage;
import cn.renlm.graph.ws.WsMessage.WsType;
import cn.renlm.graph.ws.WsUtil;

/**
 * WebSocket广播队列
 * 
 * @author Renlm
 *
 */
@Configuration
public class WsTopicQueueConfig {

	private static final String KEY = "WsTopic";

	public static final String EXCHANGE = KEY + AmqpUtil.Exchange;

	public static final String QUEUE = KEY + AmqpUtil.Queue;

	public static final String ROUTINGKEY = QUEUE + AmqpUtil.RoutingKey;

	/**
	 * 监听广播队列
	 * 
	 * @param info
	 */
	@RabbitListener(queues = "#{" + QUEUE + ".name}")
	public void receive(OshiInfo info) {
		WsUtil.topic(WsMessage.build(WsType.oshi, OshiInfoUtil.servers()));
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