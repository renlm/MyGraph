package cn.renlm.graph.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.renlm.graph.util.AmqpUtil;

/**
 * 死信队列
 * 
 * @author Renlm
 *
 */
@Configuration
public class DeadLetterConfig {

	private static final String key = "DeadLetter";

	public static final String exchange = key + AmqpUtil.Exchange;

	public static final String queue = key + AmqpUtil.Queue;

	public static final String routingKey = queue + AmqpUtil.RoutingKey;

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
	public Queue deadLetterQueue() {
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