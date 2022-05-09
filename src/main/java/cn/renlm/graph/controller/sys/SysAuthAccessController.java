package cn.renlm.graph.controller.sys;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.response.Result;
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
	 * @param authAccessed
	 * @param roleId
	 * @param root
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(boolean authAccessed, String roleId, boolean root, Long id) {
		return sysAuthAccessService.getTree(authAccessed, roleId, root, id);
	}

	/**
	 * 添加授权
	 * 
	 * @param roleId
	 * @param resourceIds
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/grant")
	public Result<?> grant(String roleId, String resourceIds) {
		try {
			sysAuthAccessService.grant(roleId, StrUtil.splitTrim(resourceIds, COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 取消授权
	 * 
	 * @param roleId
	 * @param resourceIds
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/unGrant")
	public Result<?> unGrant(String roleId, String resourceIds) {
		try {
			sysAuthAccessService.unGrant(roleId, StrUtil.splitTrim(resourceIds, COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}