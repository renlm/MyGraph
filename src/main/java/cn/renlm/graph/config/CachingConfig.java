package cn.renlm.graph.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Caffeine;

import cn.hutool.core.util.StrUtil;

/**
 * 缓存配置
 * 
 * @author Renlm
 *
 */
@Configuration(proxyBeanMethods = false)
public class CachingConfig implements CachingConfigurer {

	public static final String DEFAULT_CACHE_MANAGER = "cacheManager";

	public static final String DEFAULT_KEY_GENERATOR = "keyGenerator";

	public static final String DEFAULT_CACHE_NAME = "DEFAULT";
	
	public static final String DICT_CACHE_NAME = "DICT";

	public enum CacheEnum {

		DEFAULT(300, 10000, 200), DICT(60 * 60 * 24 * 7, 10000, 200);

		private int second;
		private long maxSize;
		private int initSize;

		CacheEnum(int second, long maxSize, int initSize) {
			this.second = second;
			this.maxSize = maxSize;
			this.initSize = initSize;
		}

	}

	@Primary
	@Bean(DEFAULT_CACHE_MANAGER)
	@Override
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		ArrayList<CaffeineCache> caffeineCaches = new ArrayList<>();
		for (CacheEnum cacheEnum : CacheEnum.values()) {
			caffeineCaches.add(new CaffeineCache(cacheEnum.name(),
					// 构建本地缓存
					Caffeine.newBuilder()
							// 设置最后一次写入或访问后经过固定时间过期
							.expireAfterWrite(Duration.ofSeconds(cacheEnum.second))
							// 初始的缓存空间大小
							.initialCapacity(cacheEnum.initSize)
							// 缓存的最大条数
							.maximumSize(cacheEnum.maxSize)
							// 创建
							.build()));
		}
		cacheManager.setCaches(caffeineCaches);
		return cacheManager;
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
