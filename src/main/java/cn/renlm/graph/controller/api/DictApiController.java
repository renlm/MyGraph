package cn.renlm.graph.controller.api;

import static cn.renlm.graph.common.CacheKey.DictApi;
import static cn.renlm.graph.config.CachingConfig.cacheManager;
import static cn.renlm.graph.config.CachingConfig.keyGenerator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ArrayUtil;
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
@CacheConfig(cacheNames = DictApi, cacheManager = cacheManager, keyGenerator = keyGenerator)
public class DictApiController {

	@Autowired
	private ISysDictService iSysDictService;

	/**
	 * 获取子集列表
	 * 
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getChildren")
	@Caching(cacheable = { @Cacheable })
	public List<SysDict> getChildren(String uuid) {
		if (StrUtil.isBlank(uuid)) {
			return CollUtil.newArrayList();
		}
		SysDict sysDict = iSysDictService.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getUuid, uuid));
		if (sysDict == null) {
			return CollUtil.newArrayList();
		} else {
			return iSysDictService.findListByPid(sysDict.getId());
		}
	}

	/**
	 * 获取下级列表
	 * 
	 * @param pcodes
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getItem")
	@Caching(cacheable = { @Cacheable })
	public List<SysDict> getItem(String pcodes) {
		String[] codePaths = StrUtil.splitToArray(pcodes, StrUtil.COMMA);
		if (ArrayUtil.isEmpty(codePaths)) {
			return CollUtil.newArrayList();
		}
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
	@Caching(cacheable = { @Cacheable })
	public List<Tree<Long>> getTree(String pcodes) {
		String[] codePaths = StrUtil.splitToArray(pcodes, StrUtil.COMMA);
		if (ArrayUtil.isEmpty(codePaths)) {
			return CollUtil.newArrayList();
		}
		List<Tree<Long>> tree = iSysDictService.getTree(codePaths);
		return tree;
	}
}