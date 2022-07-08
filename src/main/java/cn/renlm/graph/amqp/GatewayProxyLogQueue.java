package cn.renlm.graph.amqp;

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

import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyLogService;
import cn.renlm.graph.util.Ip2regionUtil;

/**
 * 网关代理日志记录
 * 
 * @author Renlm
 *
 */
@Configuration
public class GatewayProxyLogQueue {

	private static final String KEY = "GatewayProxyLog";

	public static final String EXCHANGE = KEY + AmqpUtil.Exchange;

	public static final String QUEUE = KEY + AmqpUtil.Queue;

	public static final String ROUTINGKEY = QUEUE + AmqpUtil.RoutingKey;

	@Autowired
	private IGatewayProxyLogService iGatewayProxyLogService;

	/**
	 * 写入日志
	 * 
	 * @param proxyLog
	 */
	@RabbitListener(concurrency = "#{myConfigProperties.proxyLogConcurrency}", bindings = {
			@QueueBinding(value = @Queue(value = QUEUE, durable = Exchange.TRUE), exchange = @Exchange(value = EXCHANGE, type = ExchangeTypes.DIRECT), key = ROUTINGKEY) })
	public void receiveMessage(GatewayProxyLog proxyLog) {
		proxyLog.setIpRegion(Ip2regionUtil.parse(proxyLog.getClientIp()));
		iGatewayProxyLogService.save(proxyLog);
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