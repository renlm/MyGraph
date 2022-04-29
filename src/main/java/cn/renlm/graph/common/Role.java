package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 关键角色
 * 
 * @author Renlm
 *
 */
public enum Role {

	PLATFORM("平台管理"), SUPER("超级管理员"), SELF("自主注册");

	public static final String HAS_ROLE_PREFIX = "ROLE_";

	@Getter
	private final String text;

	private Role(String text) {
		this.text = text;
	}
}