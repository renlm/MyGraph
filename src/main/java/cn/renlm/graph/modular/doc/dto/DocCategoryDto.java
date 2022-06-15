package cn.renlm.graph.modular.doc.dto;

import cn.renlm.graph.modular.doc.entity.DocCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文档分类
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DocCategoryDto extends DocCategory {

	private static final long serialVersionUID = 1L;

}