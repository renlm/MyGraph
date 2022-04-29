package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDto extends SysRole {

	private static final long serialVersionUID = 1L;

	private Boolean isLeaf;

}