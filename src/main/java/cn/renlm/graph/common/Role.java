package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 关键角色
 * 
 * @author Renlm
 *
 */
public enum Role {

	PLATFORM("平台管理"), 
	SUPER("超级管理员"), 
	COMMON("通用权限"), 
	GENERAL("普通用户");

	/**
	 * 角色前缀
	 */
	public static final String HAS_ROLE_PREFIX = "ROLE_";

	/**
	 * 管理权限
	 */
	public static final String AdminSpEL = "hasRole('SUPER')";

	@Getter
	private final String text;

	private Role(String text) {
		this.text = text;
	}

}
