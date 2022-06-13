package cn.renlm.graph.modular.doc.dto;

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

	private String keywords;

}