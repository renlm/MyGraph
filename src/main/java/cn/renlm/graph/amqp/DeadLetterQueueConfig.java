package cn.renlm.graph.amqp;

import java.util.Date;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.util.AmqpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 死信队列
 * 
 * @author Renlm
 *
 */
@Slf4j
@Component
@Configuration
public class DeadLetterQueueConfig {

	private static final String key = "DeadLetter";

	public static final String exchange = key + AmqpUtil.Exchange;

	public static final String queue = key + AmqpUtil.Queue;

	public static final String routingKey = queue + AmqpUtil.RoutingKey;

	/**
	 * 监听队列
	 * 
	 * @param message
	 * @param channel
	 */
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = queue, durable = Exchange.TRUE), exchange = @Exchange(value = exchange, type = ExchangeTypes.DIRECT), key = routingKey) })
	public void receiveMessage(Message message, Channel channel) {
		String body = StrUtil.str(message.getBody(), message.getMessageProperties().getContentEncoding());
		log.info("当前时间：{}，收到死信队列消息：{}", DateUtil.formatDateTime(new Date()), body);
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
		return QueueBuilder.durable(queue).build();
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