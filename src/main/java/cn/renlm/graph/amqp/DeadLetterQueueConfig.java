package cn.renlm.graph.amqp;

import java.util.Date;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.amqp.AmqpUtil.DelayTask;
import cn.renlm.graph.amqp.AmqpUtil.DelayTaskParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 死信队列
 * 
 * @author Renlm
 *
 */
@Slf4j
@Configuration
public class DeadLetterQueueConfig {

	private static final String KEY = "DeadLetter";

	public static final String EXCHANGE = KEY + AmqpUtil.Exchange;

	public static final String QUEUE = KEY + AmqpUtil.Queue;

	public static final String ROUTINGKEY = QUEUE + AmqpUtil.RoutingKey;

	@Autowired
	private AmqpTemplate amqpTemplate;

	/**
	 * 监听队列
	 * 
	 * @param delayTask
	 */
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = QUEUE, durable = Exchange.TRUE), exchange = @Exchange(value = EXCHANGE, type = ExchangeTypes.DIRECT), key = ROUTINGKEY) })
	public void receiveMessage(DelayTaskParam<Object> taskParam) {
		String receiveTime = DateUtil.formatDateTime(new Date());
		log.info("=== 死信队列，接收时间：{}\r\n=== 消息内容：{}", receiveTime, JSONUtil.toJsonStr(taskParam));
		String time = DateUtil.formatDateTime(taskParam.getTime());
		// 本地任务（反射执行方法）
		if (NumberUtil.equals(0, taskParam.getType())) {
			if (StrUtil.isNotBlank(taskParam.getDelayTaskClass())) {
				Class<DelayTask> delayTaskClass = ClassUtil.loadClass(taskParam.getDelayTaskClass());
				if (delayTaskClass == null) {
					log.error("=== 死信队列，接收时间：{}\r\n=== 无效任务：{}", receiveTime, JSONUtil.toJsonPrettyStr(taskParam));
				} else {
					log.info("=== 延时任务，接收时间：{}\r\n=== 任务类型：{}\r\n=== 创建时间：{}\r\n=== 任务执行类：{}\r\n=== 任务数据：{}",
							// 接收时间
							receiveTime,
							// 任务类型
							"本地任务（反射执行方法）",
							// 创建时间
							time,
							// 任务执行类
							taskParam.getDelayTaskClass(),
							// 任务数据
							JSONUtil.toJsonPrettyStr(taskParam.getData()));
					// 触发任务执行
					DelayTask delayTask = ReflectUtil.newInstance(delayTaskClass);
					ReflectUtil.invoke(delayTask, DelayTask.method, taskParam.getData());
				}
			} else {
				log.error("=== 死信队列，接收时间：{}\r\n=== 无效任务：{}", receiveTime, JSONUtil.toJsonPrettyStr(taskParam));
			}
		}
		// 队列任务
		else if (NumberUtil.equals(1, taskParam.getType())) {
			if (StrUtil.isNotBlank(taskParam.getExchange())) {
				log.info("=== 延时任务，接收时间：{}\r\n=== 任务类型：{}\r\n=== 创建时间：{}\r\n=== 交换机名称：{}\r\n=== 路由名称：{}\r\n=== 任务数据：{}",
						// 接收时间
						receiveTime,
						// 任务类型
						"队列任务",
						// 创建时间
						time,
						// 交换机名称
						taskParam.getExchange(),
						// 路由名称
						taskParam.getRoutingKey(),
						// 任务数据
						JSONUtil.toJsonPrettyStr(taskParam.getData()));
				// 触发任务执行队列
				amqpTemplate.convertAndSend(taskParam.getExchange(), taskParam.getRoutingKey(), taskParam.getData());
			} else {
				log.error("=== 死信队列，接收时间：{}\r\n=== 无效任务：{}", receiveTime, JSONUtil.toJsonPrettyStr(taskParam));
			}
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
		return QueueBuilder.durable(QUEUE).build();
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