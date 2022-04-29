package cn.renlm.graph.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.service.ISysDictService;

/**
 * 字典工具
 * 
 * @author Renlm
 *
 */
@Component
public class DictUtil {

	@Autowired
	private ISysDictService iSysDictService;

	/**
	 * 获取代码名称映射
	 * 
	 * @param paths
	 * @return
	 */
	public Map<String, String> CODEMAP(String paths) {
		Map<String, String> codemap = new LinkedHashMap<>();
		List<SysDict> parents = iSysDictService.findPaths(StrUtil.splitToArray(paths, StrUtil.COMMA));
		CollUtil.removeNull(parents);
		if (CollUtil.isEmpty(parents)) {
			return codemap;
		}
		List<SysDict> items = iSysDictService
				.list(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getPid, CollUtil.getLast(parents).getId()));
		items.forEach(it -> {
			codemap.put(it.getCode(), it.getText());
		});
		return codemap;
	}
}