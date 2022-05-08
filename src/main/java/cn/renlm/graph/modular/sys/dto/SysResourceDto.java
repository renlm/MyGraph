package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 资源
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysResourceDto extends SysResource {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否授权
	 */
	private Boolean accessAuth;

}