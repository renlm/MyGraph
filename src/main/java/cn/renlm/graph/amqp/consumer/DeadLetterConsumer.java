package cn.renlm.graph.amqp.consumer;

import java.util.Date;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Cache.Channel;
import org.springframework.stereotype.Component;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 监听死信队列
 * 
 * @author Renlm
 *
 */
@Slf4j
@Component
public class DeadLetterConsumer {

	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = "#{deadLetterBinding.destination}", durable = Exchange.TRUE), exchange = @Exchange(value = "#{deadLetterBinding.exchange}", type = ExchangeTypes.DIRECT), key = "#{deadLetterBinding.routingKey}") })
	public void receiveMessage(Message message, Channel channel) {
		log.info("当前时间：{}，收到死信队列消息：{}", DateUtil.formatTime(new Date()), message);
	}
}