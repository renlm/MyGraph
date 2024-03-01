package cn.renlm.graph.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.CachingConfigurer;
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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 缓存配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class CachingConfig implements CachingConfigurer {

	public static final String DEFAULT_CACHE_MANAGER = "cacheManager";

	public static final String DEFAULT_KEY_GENERATOR = "keyGenerator";

	private final RedisConnectionFactory connectionFactory;

	@Bean
	public Cache<String, ?> caffeineCache() {
		return Caffeine.newBuilder()
				// 设置最后一次写入或访问后经过固定时间过期
				.expireAfterWrite(300, TimeUnit.SECONDS)
				// 初始的缓存空间大小
				.initialCapacity(100)
				// 缓存的最大条数
				.maximumSize(1000)
				// 创建
				.build();
	}

	@Primary
	@Bean(DEFAULT_CACHE_MANAGER)
	@Override
	public RedisCacheManager cacheManager() {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(30))
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(new GenericJackson2JsonRedisSerializer()))
				.disableCachingNullValues();
		RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory).cacheDefaults(config)
				.transactionAware().build();
		return redisCacheManager;
	}

	@Primary
	@Bean(DEFAULT_KEY_GENERATOR)
	@Override
	public KeyGenerator keyGenerator() {
		return (target, method, params) -> {
			StringBuilder sb = new StringBuilder();
			sb.append(target.getClass().getName());
			sb.append(StrUtil.DOT);
			sb.append(method.getName());
			sb.append(StrUtil.DOT);
			sb.append(Arrays.deepHashCode(params));
			return sb.toString();
		};
	}

}
