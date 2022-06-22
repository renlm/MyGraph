package cn.renlm.graph.modular.graph.dto;

import java.math.BigDecimal;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.graph.entity.Graph;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图形设计
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GraphDto extends Graph {

	private static final long serialVersionUID = 1L;

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