package cn.renlm.mygraph.modular.sys.dto;

import cn.renlm.mygraph.modular.sys.entity.SysResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 资源
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysResourceDto extends SysResource {

	private static final long serialVersionUID = 1L;

	/**
	 * 别名
	 */
	private String alias;

	/**
	 * 是否授权
	 */
	private Boolean accessAuth;

}