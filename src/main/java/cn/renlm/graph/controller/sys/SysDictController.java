package cn.renlm.graph.controller.sys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.modular.sys.service.ISysDictService;

/**
 * 数据字典
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/dict")
public class SysDictController {

	@Autowired
	private ISysDictService iSysDictService;

	/**
	 * 字典列表
	 * 
	 * @return
	 */
	@GetMapping
	public String index() {
		return "sys/dict";
	}

	/**
	 * 获取树形字典
	 * 
	 * @param parentId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(Long parentId) {
		List<Tree<Long>> tree = iSysDictService.getTree(parentId);
		return tree;
	}
}