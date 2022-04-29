package cn.renlm.graph.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * 字典工具
 * 
 * @author Renlm
 *
 */
@Component
public class DictUtil {

	/**
	 * 获取代码名称映射
	 * 
	 * @param paths
	 * @return
	 */
	public Map<String, String> CODEMAP(String paths) {
		Map<String, String> codemap = new LinkedHashMap<>();
		return codemap;
	}
}