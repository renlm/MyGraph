package cn.renlm.graph.controller;

import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.common.CacheKey;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.oshi.OshiInfo;
import cn.renlm.graph.oshi.OshiInfoUtil;
import cn.renlm.graph.security.UserService;
import cn.renlm.graph.ws.WsUtil;

/**
 * Home
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping
public class HomeController {

	@Autowired
	private UserService userService;

	@Resource
	private RedisTemplate<String, Integer> redisTemplate;

	/**
	 * 主页
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping
	public String index(ModelMap model) {
		User user = userService.refreshAuthentication();
		model.put("navGroup", user.getNavGroup());
		model.put("homePages", user.getHomePages());
		return "index";
	}

	/**
	 * 在线调试
	 * 
	 * @return
	 */
	@GetMapping("/compile")
	public String compile() {
		return "compile";
	}

	/**
	 * 欢迎页
	 * 
	 * @return
	 */
	@GetMapping("/home/welcome")
	public String welcome() {
		return "home/welcome";
	}

	/**
	 * 服务器监控
	 * 
	 * @param model
	 * @param authentication
	 * @param type
	 * @return
	 */
	@GetMapping("/home/oshi")
	public String oshi(ModelMap model, Authentication authentication, Integer type) {
		User user = (User) authentication.getPrincipal();
		String cacheTypeKey = CacheKey.OshiType.name() + StrUtil.AT + user.getUserId();
		if (type == null) {
			type = redisTemplate.opsForValue().get(cacheTypeKey);
		} else {
			redisTemplate.opsForValue().set(cacheTypeKey, type);
		}
		long onlineUserNumber = WsUtil.getOnlineUserNumber();
		Map<String, Set<OshiInfo>> oshiInfos = OshiInfoUtil.get();
		model.put("onlineUserNumber", onlineUserNumber);
		model.put("oshiInfos", JSONUtil.toJsonStr(oshiInfos));
		return "home/oshi." + ObjectUtil.defaultIfNull(type, 1);
	}
}