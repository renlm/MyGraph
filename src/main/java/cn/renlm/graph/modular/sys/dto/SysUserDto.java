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
	 * 所属组织机构ID
	 */
	private String orgId;

	/**
	 * 搜索关键词
	 */
	private String keywords;

}