package cn.renlm.graph.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.modular.sys.dto.SysResourceDto;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.util.TreeExtraUtil;

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
		Map<Long, SysResourceDto> authAccessedMap = new LinkedHashMap<>();
		List<SysResourceDto> authAccessed = iSysResourceService.findListByRole(roleId);
		authAccessed.forEach(srd -> {
			authAccessedMap.put(srd.getId(), srd);
		});
		List<Tree<Long>> tree = iSysResourceService.getTree(root, pid);
		TreeExtraUtil.foreach(tree, node -> {
			node.putExtra("accessAuth", authAccessedMap.containsKey(node.getId()));
		});
		return tree;
	}
}