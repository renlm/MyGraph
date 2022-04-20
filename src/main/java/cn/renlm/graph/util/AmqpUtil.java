package cn.renlm.graph.util;

import cn.hutool.core.lang.Assert;
import lombok.experimental.UtilityClass;

/**
 * 消息队列
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class AmqpUtil {

	/**
	 * 延时任务最大时长（7天）
	 */
	public static final int maxDelayTtl = 1000 * 60 * 60 * 24 * 7;

	/**
	 * 交换机名称后缀
	 */
	public static final String Exchange = "Exchange";

	/**
	 * 队列名称后缀
	 */
	public static final String Queue = "Queue";

	/**
	 * 路由名称后缀
	 */
	public static final String RoutingKey = "RoutingKey";

	/**
	 * 拼接交换机名称
	 * 
	 * @param key
	 * @return
	 */
	public static final String exchangeName(String key) {
		Assert.notBlank(key, "key不能为空");
		return key + Exchange;
	}

	/**
	 * 拼接队列名称
	 * 
	 * @param key
	 * @return
	 */
	public static final String queueName(String key) {
		Assert.notBlank(key, "key不能为空");
		return key + Queue;
	}

	/**
	 * 拼接路由名称
	 * 
	 * @param key
	 * @return
	 */
	public static final String routingKeyName(String key) {
		Assert.notBlank(key, "key不能为空");
		return queueName(key) + RoutingKey;
	}
}