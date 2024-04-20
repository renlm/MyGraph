package cn.renlm.mygraph.modular.graph.dto;

import java.math.BigDecimal;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.modular.graph.entity.Graph;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图形设计
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GraphDto extends Graph {

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
	private String parentsCategorName;

	/**
	 * 关键字
	 */
	private String keywords;

	/**
	 * 图形默认配置
	 * 
	 * @param graph
	 */
	public static final void fillDefault(Graph graph) {
		graph.setZoom(ObjectUtil.defaultIfNull(graph.getZoom(), new BigDecimal(1)));
		graph.setDx(ObjectUtil.defaultIfNull(graph.getDx(), 0));
		graph.setDy(ObjectUtil.defaultIfNull(graph.getDy(), 0));
		graph.setGridEnabled(ObjectUtil.defaultIfNull(graph.getGridEnabled(), true));
		graph.setGridSize(ObjectUtil.defaultIfNull(graph.getGridSize(), 1));
		graph.setPageVisible(ObjectUtil.defaultIfNull(graph.getPageVisible(), false));
		graph.setBackground(StrUtil.isBlank(graph.getBackground()) ? null : graph.getBackground());
		graph.setConnectionArrowsEnabled(ObjectUtil.defaultIfNull(graph.getConnectionArrowsEnabled(), false));
		graph.setConnectable(ObjectUtil.defaultIfNull(graph.getConnectable(), true));
		graph.setGuidesEnabled(ObjectUtil.defaultIfNull(graph.getGuidesEnabled(), true));
	}
}