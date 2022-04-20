package cn.renlm.graph.common;

import cn.hutool.core.lang.Assert;

/**
 * 消息队列后缀
 * 
 * @author Renlm
 *
 */
public enum AmqpSuffix {

	Exchange, Queue, RoutingKey;

	/**
	 * 拼接交换机名称
	 * 
	 * @param key
	 * @return
	 */
	public static final String exchangeName(String key) {
		Assert.notBlank(key);
		return key + Exchange;
	}

	/**
	 * 拼接队列名称
	 * 
	 * @param key
	 * @return
	 */
	public static final String queueName(String key) {
		Assert.notBlank(key);
		return key + Queue;
	}

	/**
	 * 拼接路由名称
	 * 
	 * @param key
	 * @return
	 */
	public static final String routingKeyName(String key) {
		Assert.notBlank(key);
		return queueName(key) + RoutingKey;
	}
}