package cn.renlm.graph.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReUtil;
import cn.renlm.graph.common.CacheKey;

/**
 * Font Awesome
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/fontAwesome")
public class FontAwesomeController {

	@Resource
	private RedisTemplate<String, List<String>> redisTemplate;

	/**
	 * 图标集
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getIcons")
	public List<String> getIcons() {
		List<String> cached = redisTemplate.opsForValue().get(CacheKey.FontAwesomeIcons.name());
		if (CollUtil.isNotEmpty(cached)) {
			return cached;
		}
		String css = ResourceUtil.readUtf8Str("META-INF/resources/webjars/font-awesome/4.7.0/css/font-awesome.min.css");
		List<String> icons = ReUtil.findAll("\\.([^{}]*?):before", css, 1);
		redisTemplate.opsForValue().set(CacheKey.FontAwesomeIcons.name(), icons, 7, TimeUnit.DAYS);
		return icons;
	}
}