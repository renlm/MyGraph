package cn.renlm.graph.controller.sys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.service.SysAuthAccessService;

/**
 * 角色授权
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/authAccess")
public class SysAuthAccessController {

	@Autowired
	private SysAuthAccessService sysAuthAccessService;

	/**
	 * 获取角色授权资源列表
	 * 
	 * @param roleId
	 * @param root
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(String roleId, boolean root, Long id) {
		return sysAuthAccessService.getTree(roleId, root, id);
	}
}