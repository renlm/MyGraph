package cn.renlm.graph.controller;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.json.JSONUtil;
import cn.renlm.graph.oshi.OshiInfo;
import cn.renlm.graph.oshi.OshiInfoUtil;
import cn.renlm.graph.ws.WsUtil;

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
	 * @param model
	 * @return
	 */
	@GetMapping
	public String home(ModelMap model) {
		long onlineUserNumber = WsUtil.getOnlineUserNumber();
		Map<String, Set<OshiInfo>> oshiInfos = OshiInfoUtil.get();
		model.put("onlineUserNumber", onlineUserNumber);
		model.put("oshiInfos", JSONUtil.toJsonStr(oshiInfos));
		return "oshi";
	}
}