package cn.renlm.mygraph.common;

import lombok.Getter;

/**
 * 菜单状态
 * 
 * @author RenLiMing(任黎明)
 *
 */
public enum TreeState {

	open("无子菜单"), 
	closed("有子菜单");

	@Getter
	private final String text;

	private TreeState(String text) {
		this.text = text;
	}

}
