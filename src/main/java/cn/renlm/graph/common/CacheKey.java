package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 缓存Key
 * 
 * @author Renlm
 *
 */
public enum CacheKey {

	SysConst("系统常量"), 
	FontAwesomeIcons("Font Awesome 图标集"), 
	OshiType("服务器监控");
	
	// 字典缓存
	public static final String DICT_CACHE_NAME = "DICT";

	@Getter
	private final String text;

	private CacheKey(String text) {
		this.text = text;
	}

}
