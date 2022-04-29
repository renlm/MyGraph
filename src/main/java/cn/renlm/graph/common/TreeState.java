package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 菜单状态
 * 
 * @author Renlm
 *
 */
public enum TreeState {

	open("无子菜单"), closed("有子菜单");

	@Getter
	private final String text;

	private TreeState(String text) {
		this.text = text;
	}
}