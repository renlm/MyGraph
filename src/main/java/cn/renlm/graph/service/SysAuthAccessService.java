package cn.renlm.graph.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.modular.sys.service.ISysResourceService;

/**
 * 角色授权
 * 
 * @author Renlm
 *
 */
@Service
public class SysAuthAccessService {

	@Autowired
	private ISysResourceService iSysResourceService;

	/**
	 * 获取角色授权资源列表
	 * 
	 * @param roleId
	 * @param root
	 * @param pid
	 * @return
	 */
	public List<Tree<Long>> getTree(String roleId, boolean root, Long pid) {
		List<Tree<Long>> tree = iSysResourceService.getTree(root, pid);
		return tree;
	}
}