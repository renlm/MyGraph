package cn.renlm.graph;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.db.nosql.redis.RedisDS;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Jedis工具
 *
 * @author Renlm
 *
 */
@Slf4j
public class JedisTest {

	private String key = this.getClass().getName();

	private Jedis jedis = RedisDS.create().getJedis();

	/**
	 * 为指定的 key 设置值及其过期时间。如果 key 已经存在，SETEX 命令将会替换旧的值
	 */
	@Test
	public void setex() {
		String reply = jedis.setex(key, 5L, IdUtil.getSnowflakeNextIdStr());
		log.info("保存数据：{}", reply);
		AtomicInteger i = new AtomicInteger();
		CronUtil.schedule("*/1 * * * * ?", new Task() {
			@Override
			public void execute() {
				log.info("{}.获取数据：{}", i.incrementAndGet(), jedis.get(key));
			}
		});
		CronUtil.setMatchSecond(true);
		CronUtil.start();
		ThreadUtil.safeSleep(6000);
	}

	/**
	 * 模拟实时统计在线用户人数
	 */
	@Test
	public void zadd() {
		Long validityMillis = 30 * 1000L;
		Long expTime = DateUtil.current() + validityMillis;
		String snowflakeIdStr = IdUtil.getSnowflakeNextIdStr();
		Long reply10 = jedis.zadd(key, expTime, snowflakeIdStr);
		log.info("上线，添加或更新当前uid：{}", reply10);
		Long reply11 = jedis.zadd(key, expTime, snowflakeIdStr);
		log.info("上线，添加或更新当前uid：{}", reply11);
		Long reply20 = jedis.zremrangeByScore(key, 0, DateUtil.current());
		log.info("删除小于等于当前时间的记录：{}", reply20);
		log.info("当前在线人数：{}", jedis.zcard(key));
		Long reply30 = jedis.zrem(key, snowflakeIdStr);
		log.info("离线，删除当前uid：{}", reply30);
		log.info("当前在线人数：{}", jedis.zcard(key));
	}
}