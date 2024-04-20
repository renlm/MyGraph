package cn.renlm.mygraph.amqp;

import java.io.Serializable;
import java.util.Date;

import org.springframework.amqp.core.AmqpTemplate;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

/**
 * 消息队列
 * 
 * @author RenLiMing(任黎明)
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
	 * 添加队列
	 * 
	 * @param <T>
	 * @param exchange
	 * @param routingKey
	 * @param data
	 */
	public static final <T> void createQueue(String exchange, String routingKey, T data) {
		Assert.notBlank(exchange, "exchange不能为空");
		Assert.notBlank(routingKey, "routingKey不能为空");
		Assert.notNull(data, "data不能为空");
		AmqpTemplate amqpTemplate = SpringUtil.getBean(AmqpTemplate.class);
		amqpTemplate.convertAndSend(exchange, routingKey, data);
	}

	/**
	 * 创建延时任务
	 * 
	 * @param taskClass 任务执行类
	 * @param data      任务数据
	 * @param delayTtl  延时ttl时长（毫秒数）
	 */
	public static final <T> void createDelayTask(Class<? extends DelayTask> taskClass, T data, int delayTtl) {
		Assert.notNull(taskClass, "延时任务taskClass不能为空");
		Date time = new Date();
		long day = DateUtil.between(time, DateUtil.offsetMillisecond(time, AmqpUtil.maxDelayTtl), DateUnit.DAY);
		Assert.isFalse(delayTtl > AmqpUtil.maxDelayTtl, "延时任务最大时长（" + day + "天）");
		DelayTaskParam<T> taskParam = new DelayTaskParam<T>();
		taskParam.setType(0);
		taskParam.setTime(time);
		taskParam.setDelayTaskClass(taskClass.getName());
		taskParam.setData(data);
		AmqpTemplate amqpTemplate = SpringUtil.getBean(AmqpTemplate.class);
		amqpTemplate.convertAndSend(TtlQueue.EXCHANGE, TtlQueue.ROUTINGKEY, taskParam, message -> {
			message.getMessageProperties().setExpiration(String.valueOf(delayTtl));
			return message;
		});
	}

	/**
	 * 创建延时任务
	 * 
	 * @param exchange   任务交换机名称
	 * @param routingKey 任务路由名称
	 * @param data       任务数据
	 * @param delayTtl   延时ttl时长（毫秒数）
	 */
	public static final <T> void createDelayTask(String exchange, String routingKey, T data, int delayTtl) {
		Assert.notBlank(exchange, "延时任务exchange不能为空");
		Assert.notBlank(routingKey, "延时任务routingKey不能为空");
		Date time = new Date();
		long day = DateUtil.between(time, DateUtil.offsetMillisecond(time, AmqpUtil.maxDelayTtl), DateUnit.DAY);
		Assert.isFalse(delayTtl > AmqpUtil.maxDelayTtl, "延时任务最大时长（" + day + "天）");
		DelayTaskParam<T> taskParam = new DelayTaskParam<T>();
		taskParam.setType(1);
		taskParam.setTime(time);
		taskParam.setExchange(exchange);
		taskParam.setRoutingKey(routingKey);
		taskParam.setData(data);
		AmqpTemplate amqpTemplate = SpringUtil.getBean(AmqpTemplate.class);
		amqpTemplate.convertAndSend(TtlQueue.EXCHANGE, TtlQueue.ROUTINGKEY, taskParam, message -> {
			message.getMessageProperties().setExpiration(String.valueOf(delayTtl));
			return message;
		});
	}

	/**
	 * 延时任务（执行类接口）
	 */
	public static interface DelayTask {

		/**
		 * 方法名
		 */
		public static final String method = "execute";

		/**
		 * 执行任务
		 * 
		 * @param data 任务数据
		 */
		void execute(Object data);

	}

	/**
	 * 延时任务参数
	 */
	@Data
	@Accessors(chain = true)
	public static final class DelayTaskParam<T> implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 类型，0：本地任务（反射执行方法），1：队列任务
		 */
		private int type;

		/**
		 * 添加时间
		 */
		private Date time;

		/**
		 * 本地任务-任务执行类
		 */
		private String delayTaskClass;

		/**
		 * 队列任务-交换机名称
		 */
		private String exchange;

		/**
		 * 队列任务-路由名称
		 */
		private String routingKey;

		/**
		 * 任务数据
		 */
		private T data;

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