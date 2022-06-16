package cn.renlm.graph.modular.doc.dto;

import cn.renlm.graph.modular.doc.entity.DocCategoryShare;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文档分类-分享
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DocCategoryShareDto extends DocCategoryShare {

	private static final long serialVersionUID = 1L;

	/**
	 * 项目Uuid
	 */
	private String docProjectUuid;

	/**
	 * 分类Uuid
	 */
	private String docCategoryUuid;

	/**
	 * 关键字
	 */
	private String keywords;

}