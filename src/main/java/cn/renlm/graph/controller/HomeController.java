package cn.renlm.graph.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.json.JSONUtil;
import cn.renlm.graph.oshi.OshiInfo;
import cn.renlm.graph.oshi.OshiInfoUtil;
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
	 * @return
	 */
	@GetMapping("/home/oshi")
	public String oshi(ModelMap model) {
		long onlineUserNumber = WsUtil.getOnlineUserNumber();
		Map<String, Set<OshiInfo>> oshiInfos = OshiInfoUtil.get();
		model.put("onlineUserNumber", onlineUserNumber);
		model.put("oshiInfos", JSONUtil.toJsonStr(oshiInfos));
		return "home/oshi";
	}
}