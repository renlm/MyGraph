package cn.renlm.mygraph.modular.markdown.dto;

import cn.renlm.mygraph.modular.markdown.entity.MarkdownHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Markdown文档-历史记录
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MarkdownHistoryDto extends MarkdownHistory {

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
	 * 分类Id
	 */
	private Long docCategoryId;

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
	private String parentsDocCategorName;

	/**
	 * 关键字
	 */
	private String keywords;

}