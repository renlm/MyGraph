package cn.renlm.graph.amqp;

import java.util.Date;

import org.springframework.amqp.core.AmqpTemplate;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.util.AmqpUtil;
import cn.renlm.graph.util.AmqpUtil.DelayTaskParam;
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

	@Autowired
	private AmqpTemplate amqpTemplate;

	/**
	 * 监听队列
	 * 
	 * @param message
	 * @param channel
	 */
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = queue, durable = Exchange.TRUE), exchange = @Exchange(value = exchange, type = ExchangeTypes.DIRECT), key = routingKey) })
	public void receiveMessage(Message message, Channel channel) {
		String receiveTime = DateUtil.formatDateTime(new Date());
		String body = StrUtil.str(message.getBody(), message.getMessageProperties().getContentEncoding());
		log.info("=== 死信队列，接收时间：{}\r\n=== 消息内容：{}", receiveTime, body);
		if (JSONUtil.isTypeJSONObject(body)) {
			DelayTaskParam param = JSONUtil.toBean(body, DelayTaskParam.class);
			if (StrUtil.isNotBlank(param.getExchange())) {
				String time = DateUtil.formatDateTime(param.getTime());
				log.info("=== 延时任务，接收时间：{}\r\n=== 添加时间：{}\r\n=== 交换机名称：{}\r\n=== 路由名称：{}\r\n=== 任务参数：{}",
						// 接收时间
						receiveTime,
						// 添加时间
						time,
						// 交换机名称
						param.getExchange(),
						// 路由名称
						param.getRoutingKey(),
						// 任务参数
						JSONUtil.toJsonPrettyStr(param.getParamJson()));
				// 触发任务执行队列
				amqpTemplate.convertAndSend(param.getExchange(), param.getRoutingKey(), param.getParamJson());
			} else {
				log.error("=== 死信队列，接收时间：{}\r\n=== 无效任务：{}", receiveTime, JSONUtil.toJsonPrettyStr(param));
			}
		} else {
			log.error("=== 死信队列，接收时间：{}\r\n=== 无效任务：{}", receiveTime, body);
		}
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