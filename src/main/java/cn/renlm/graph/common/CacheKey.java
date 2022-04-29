package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 缓存Key
 * 
 * @author Renlm
 *
 */
public enum CacheKey {

	SysConst("系统常量");

	@Getter
	private final String text;

	private CacheKey(String text) {
		this.text = text;
	}
}