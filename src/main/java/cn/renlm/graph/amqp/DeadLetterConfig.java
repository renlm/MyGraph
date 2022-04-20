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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cn.hutool.core.date.DateUtil;
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
public class DeadLetterConfig {

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
			@QueueBinding(value = @Queue(value = DeadLetterConfig.queue, durable = Exchange.TRUE), exchange = @Exchange(value = DeadLetterConfig.exchange, type = ExchangeTypes.DIRECT), key = DeadLetterConfig.routingKey) })
	public void receiveMessage(Message message, Channel channel) {
		log.info("当前时间：{}，收到死信队列消息：{}", DateUtil.formatDateTime(new Date()), message);
	}

	/**
	 * 声明交换机
	 * 
	 * @return
	 */
	@Bean
	public DirectExchange deadLetterExchange() {
		return ExchangeBuilder.directExchange(exchange).durable(true).build();
	}

	/**
	 * 声明队列
	 * 
	 * @return
	 */
	@Bean
	public org.springframework.amqp.core.Queue deadLetterQueue() {
		return QueueBuilder.durable(queue).build();
	}

	/**
	 * 绑定队列到交换机
	 * 
	 * @return
	 */
	@Bean
	public Binding deadLetterBinding() {
		return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(routingKey);
	}
}