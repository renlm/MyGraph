package cn.renlm.graph.util;

import java.io.Serializable;
import java.util.Date;

import org.springframework.amqp.core.AmqpTemplate;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.amqp.TtlQueueConfig;
import lombok.Data;
import lombok.experimental.Accessors;
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
	 * 创建延时任务
	 * 
	 * @param exchange   任务交换机名称
	 * @param routingKey 任务路由名称
	 * @param paramJson  任务参数（Json格式）
	 * @param delayTtl   延时ttl时长（毫秒数）
	 */
	public static final void createDelayTask(String exchange, String routingKey, String paramJson, int delayTtl) {
		Assert.notBlank(exchange, "延时任务exchange不能为空");
		Assert.isTrue(JSONUtil.isTypeJSON(paramJson), "任务参数paramJson必须为Json格式");
		Date time = new Date();
		long day = DateUtil.between(time, DateUtil.offsetMillisecond(time, AmqpUtil.maxDelayTtl), DateUnit.DAY);
		Assert.isFalse(delayTtl > AmqpUtil.maxDelayTtl, "延时任务最大时长（" + day + "天）");
		DelayTaskParam param = new DelayTaskParam();
		param.setTime(time);
		param.setExchange(exchange);
		param.setRoutingKey(routingKey);
		param.setParamJson(paramJson);
		AmqpTemplate amqpTemplate = SpringUtil.getBean(AmqpTemplate.class);
		amqpTemplate.convertAndSend(TtlQueueConfig.exchange, TtlQueueConfig.routingKey, JSONUtil.toJsonStr(param),
				message -> {
					message.getMessageProperties().setExpiration(String.valueOf(delayTtl));
					return message;
				});
	}

	/**
	 * 延时任务参数
	 */
	@Data
	@Accessors(chain = true)
	public static final class DelayTaskParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 添加时间
		 */
		private Date time;

		/**
		 * 交换机名称
		 */
		private String exchange;

		/**
		 * 路由名称
		 */
		private String routingKey;

		/**
		 * 任务参数（Json格式）
		 */
		private String paramJson;

	}

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