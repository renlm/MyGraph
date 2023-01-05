package cn.renlm.graph.util;

import org.springframework.data.redis.core.RedisTemplate;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.experimental.UtilityClass;

/**
 * 缓存辅助工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class RedisUtil {

	/**
	 * 获取Redis操作工具
	 * 
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static final <K, V> RedisTemplate<K, V> getRedisTemplate() {
		return SpringUtil.getBean(StrUtil.lowerFirst(RedisTemplate.class.getSimpleName()));
	}

}
