package cn.renlm.graph.dto;

import java.math.BigDecimal;

import cn.renlm.graph.entity.Graph;
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
		graph.setZoom(new BigDecimal(1));
		graph.setDx(0);
		graph.setDy(0);
		graph.setGridEnabled(true);
		graph.setGridSize(1);
		graph.setPageVisible(false);
		graph.setBackground(null);
		graph.setConnectionArrowsEnabled(false);
		graph.setConnectable(true);
		graph.setGuidesEnabled(true);
	}
}