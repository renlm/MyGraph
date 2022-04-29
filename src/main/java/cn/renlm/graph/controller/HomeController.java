package cn.renlm.graph.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.json.JSONUtil;
import cn.renlm.graph.common.Resource;
import cn.renlm.graph.modular.sys.dto.ResourceDto;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.oshi.OshiInfo;
import cn.renlm.graph.oshi.OshiInfoUtil;
import cn.renlm.graph.security.User;
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
	private ISysResourceService iSysResourceService;

	/**
	 * 主页
	 * 
	 * @param authentication
	 * @param model
	 * @return
	 */
	@GetMapping("/")
	public String index(Authentication authentication, ModelMap model) {
		User user = (User) authentication.getPrincipal();
		List<SysResource> homePages = iSysResourceService.findHomePagesByUserId(user.getUserId());
		List<ResourceDto> modules = iSysResourceService.findChildsByUserId(user.getUserId(), null, Resource.Type.menu,
				Resource.Type.urlNewWindows, Resource.Type.urlInsidePage, Resource.Type.more);
		model.put("homePages", homePages);
		model.put("modules", modules);
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