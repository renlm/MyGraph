package cn.renlm.graph.modular.doc.dto;

import cn.renlm.graph.modular.doc.entity.DocCategoryCollect;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文档分类-收藏
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DocCategoryCollectDto extends DocCategoryCollect {

	private static final long serialVersionUID = 1L;

	/**
	 * 项目Uuid
	 */
	private String docProjectUuid;

	/**
	 * 分类Uuid
	 */
	private String docCategoryUuid;

}