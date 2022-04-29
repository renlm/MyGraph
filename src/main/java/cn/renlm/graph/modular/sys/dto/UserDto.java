package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDto extends SysUser {

	private static final long serialVersionUID = 1L;

	/**
	 * 组织机构ID集
	 */
	private String orgIds;

	/**
	 * 角色ID集
	 */
	private String roleIds;

	/**
	 * 搜索关键词（昵称、账号、手机号）
	 */
	private String keywords;

	/**
	 * 传参角色ID
	 */
	private String paramRoleId;

	/**
	 * 是否授权
	 */
	private Boolean accessAuth;

	/**
	 * 新密码
	 */
	private String newPassword;

	/**
	 * 确认密码
	 */
	private String confirmPassword;

}