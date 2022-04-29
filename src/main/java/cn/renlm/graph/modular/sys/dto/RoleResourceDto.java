package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色资源关系
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleResourceDto extends SysRoleResource {

	private static final long serialVersionUID = 1L;

	/**
	 * 资源全路径名称
	 */
	private String resourceNames;

	/**
	 * 资源编码
	 */
	private String resourceCode;

	/**
	 * 资源类别（编码）
	 */
	private String resourceTypeCode;

}