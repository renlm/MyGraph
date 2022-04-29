package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysOrg;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织机构
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrgDto extends SysOrg {

	private static final long serialVersionUID = 1L;

	private Boolean isLeaf;

}