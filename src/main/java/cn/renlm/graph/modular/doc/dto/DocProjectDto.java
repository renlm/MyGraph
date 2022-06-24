package cn.renlm.graph.modular.doc.dto;

import java.util.List;

import cn.renlm.graph.modular.doc.entity.DocProject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文档项目
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DocProjectDto extends DocProject {

	private static final long serialVersionUID = 1L;

	/**
	 * 文档授权角色，1：浏览者，2：编辑者，3：管理员
	 */
	private Integer role;

	/**
	 * 查询条件，文档授权角色，1：浏览者，2：编辑者，3：管理员
	 */
	private List<Integer> roles;

	/**
	 * 关键字
	 */
	private String keywords;

}