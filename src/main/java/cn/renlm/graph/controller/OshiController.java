package cn.renlm.graph.controller;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.oshi.OshiInfo;
import cn.renlm.graph.oshi.OshiInfoUtil;

/**
 * 服务器信息
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/oshi")
public class OshiController {

	@Resource
	private RedisTemplate<String, OshiInfo> redisTemplate;

	/**
	 * 监控页面
	 * 
	 * @return
	 */
	@GetMapping
	public String home() {
		return "oshi";
	}

	/**
	 * 服务器列表
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/servers")
	public Map<String, Set<OshiInfo>> servers() {
		return OshiInfoUtil.servers();
	}
}