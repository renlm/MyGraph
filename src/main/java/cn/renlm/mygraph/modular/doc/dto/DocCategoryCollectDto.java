package cn.renlm.mygraph.modular.doc.dto;

import cn.renlm.mygraph.modular.doc.entity.DocCategoryCollect;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文档分类-收藏
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DocCategoryCollectDto extends DocCategoryCollect {

	private static final long serialVersionUID = 1L;

	/**
	 * 项目Id
	 */
	private Long docProjectId;

	/**
	 * 项目Uuid
	 */
	private String docProjectUuid;

	/**
	 * 项目名称
	 */
	private String docProjectName;

	/**
	 * 分类Uuid
	 */
	private String docCategoryUuid;

	/**
	 * 分类名称
	 */
	private String docCategoryName;

	/**
	 * 父级分类全路径名称
	 */
	private String parentsCategorName;

	/**
	 * 关键字
	 */
	private String keywords;

}