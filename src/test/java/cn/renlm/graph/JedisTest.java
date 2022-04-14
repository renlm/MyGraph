package cn.renlm.graph;

import org.junit.jupiter.api.Test;

import cn.hutool.core.util.IdUtil;
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
		String reply = jedis.setex(key, 10L, IdUtil.getSnowflakeNextIdStr());
		log.info(reply);
	}
}