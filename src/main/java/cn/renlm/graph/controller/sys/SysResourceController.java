package cn.renlm.graph.controller.sys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.common.Resource;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.security.UserService;

/**
 * 资源
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/resource")
public class SysResourceController {

	@Autowired
	private UserService userService;

	/**
	 * 获取菜单列表
	 * 
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getMenus")
	public List<Tree<Long>> getMenus(String uuid) {
		User user = userService.refreshAuthentication();
		List<Tree<Long>> tree = user.getResourceTree(uuid, Resource.Type.menu, Resource.Type.urlInsidePage,
				Resource.Type.urlNewWindows);
		return tree;
	}
}
