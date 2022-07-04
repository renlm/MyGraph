package cn.renlm.graph.config;

import static cn.hutool.core.text.StrPool.DOT;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import cn.renlm.graph.common.ConstVal;

/**
 * 缓存配置
 *
 * @author Renlm
 */
@Configuration
@EnableCaching
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = ConstVal.MAX_INACTIVE_INTERVAL_SECONDS)
public class CachingConfig extends CachingConfigurerSupport {

	@Bean
	@Primary
	RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1))
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(new GenericJackson2JsonRedisSerializer()))
				.disableCachingNullValues();
		RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory).cacheDefaults(config)
				.transactionAware().build();
		return redisCacheManager;
	}

	@Bean
	@Primary
	@Override
	public KeyGenerator keyGenerator() {
		return (target, method, params) -> {
			StringBuilder sb = new StringBuilder();
			sb.append(target.getClass().getName());
			sb.append(DOT);
			sb.append(method.getName());
			sb.append(DOT);
			sb.append(Arrays.deepHashCode(params));
			return sb.toString();
		};
	}
}