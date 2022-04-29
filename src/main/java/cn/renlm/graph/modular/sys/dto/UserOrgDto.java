package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysUserOrg;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户组织机构关系
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserOrgDto extends SysUserOrg {

	private static final long serialVersionUID = 1L;

	private String userId;

	private String userNickname;

	private String orgId;

	private String orgNames;

}