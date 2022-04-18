package cn.renlm.graph;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

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
}