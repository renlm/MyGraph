package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysUserDto extends SysUser {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否授权
	 */
	private Boolean accessAuth;

	/**
	 * 组织机构ID集
	 */
	private String orgIds;

	/**
	 * 角色ID集
	 */
	private String roleIds;

	/**
	 * 搜索关键词
	 */
	private String keywords;

}