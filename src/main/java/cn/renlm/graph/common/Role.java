package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 角色
 * 
 * @author Renlm
 *
 */
public enum Role {

	admin("管理员"), user("用户"), self("自主注册");

	public static final String HAS_ROLE_PREFIX = "ROLE_";

	@Getter
	private String text;

	private Role(String text) {
		this.text = text;
	}
}