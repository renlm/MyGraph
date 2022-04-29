package cn.renlm.graph.modular.sys.dto;

import java.io.Serializable;
import java.util.List;

import cn.renlm.graph.modular.sys.entity.SysOrg;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 组织机构树
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
public class OrgChartDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 父级id
	 */
	private Long pid;

	/**
	 * 名称
	 */
	private String text;

	/**
	 * 展开状态，open：无子节点、closed：有子节点
	 */
	private String state;

	/**
	 * 负责人
	 */
	private String name;

	/**
	 * 职位编码
	 */
	private String positionCode;

	/**
	 * 职位名称
	 */
	private String positionName;

	/**
	 * 手机
	 */
	private String mobile;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 组织机构ID
	 */
	private String orgId;

	/**
	 * 机构类型（编码）
	 */
	private String orgTypeCode;

	/**
	 * 由下而上的子父集
	 */
	private List<SysOrg> fathers;

}