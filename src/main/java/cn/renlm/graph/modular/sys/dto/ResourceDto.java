package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysResource;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceDto extends SysResource {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色资源关系表主键ID
	 */
	private Long sysRoleResourceId;

	/**
	 * 别名
	 */
	private String alias;

	/**
	 * 是否隐藏
	 */
	private boolean hide;

	/**
	 * 是否叶子节点
	 */
	private Boolean isLeaf;

	/**
	 * 是否授权
	 */
	private Boolean accessAuth;

}