package cn.renlm.graph.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.service.ISysDictService;

/**
 * 数据字典接口
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/api/dict")
public class DictApiController {

	@Autowired
	private ISysDictService iSysDictService;

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param pcodes
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getItem")
	public List<SysDict> getItem(String pcodes) {
		String[] codePaths = StrUtil.splitToArray(pcodes, StrUtil.COMMA);
		List<SysDict> parents = iSysDictService.findListByPath(codePaths);
		if (CollUtil.isEmpty(parents)) {
			return CollUtil.newArrayList();
		} else {
			SysDict parent = CollUtil.getLast(parents);
			return iSysDictService.findListByPid(parent.getId());
		}
	}

	/**
	 * 获取树形字典
	 * 
	 * @param pcodes
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getTree")
	public List<Tree<Long>> getTree(String pcodes) {
		String[] codePaths = StrUtil.splitToArray(pcodes, StrUtil.COMMA);
		List<Tree<Long>> tree = iSysDictService.getTree(codePaths);
		return tree;
	}
}