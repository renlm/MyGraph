package cn.renlm.graph.modular.markdown.dto;

import cn.renlm.graph.modular.markdown.entity.MarkdownHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Markdown文档-历史记录
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MarkdownHistoryDto extends MarkdownHistory {

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