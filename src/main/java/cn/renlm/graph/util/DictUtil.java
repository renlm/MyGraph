package cn.renlm.graph.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
	 * 获取字典映射
	 * 
	 * @param codePaths
	 * @return
	 */
	public String MapItemsJson(String codePaths) {
		Map<String, String> mapItems = new LinkedHashMap<>();
		List<Tree<Long>> children = iSysDictService.getTree(false, StrUtil.splitToArray(codePaths, StrUtil.COMMA));
		CollUtil.removeNull(children);
		if (CollUtil.isNotEmpty(children)) {
			TreeExtraUtil.foreach(children, node -> {
				SysDict sd = BeanUtil.copyProperties(node, SysDict.class);
				mapItems.put(sd.getCode(), sd.getText());
			});
		}
		return JSONUtil.toJsonStr(mapItems);
	}

}
