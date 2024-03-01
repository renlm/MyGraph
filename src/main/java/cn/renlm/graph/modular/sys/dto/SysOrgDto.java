package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysOrg;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 组织机构
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysOrgDto extends SysOrg {

	private static final long serialVersionUID = 1L;

	/**
	 * 负责人（昵称）
	 */
	private String leaderUserName;

	/**
	 * 负责人职位（编码）
	 */
	private String positionCode;

	/**
	 * 负责人职位（名称）
	 */
	private String positionName;

	/**
	 * 负责人手机
	 */
	private String mobile;

	/**
	 * 负责人邮箱
	 */
	private String email;

}