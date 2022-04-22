package cn.renlm.graph.oshi;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.renlm.graph.amqp.WsTopicQueueConfig;

/**
 * 服务器监控
 * 
 * @author Renlm
 *
 */
@Component
public class OshiHandler {

	@Autowired
	private AmqpTemplate amqpTemplate;

	/**
	 * 采集信息并广播
	 */
	@Scheduled(cron = OshiInfoUtil.cron)
	public void getAndTopic() {
		OshiInfo oshiInfo = OshiInfoUtil.collect();
		amqpTemplate.convertAndSend(WsTopicQueueConfig.EXCHANGE, WsTopicQueueConfig.ROUTINGKEY, oshiInfo);
	}
}