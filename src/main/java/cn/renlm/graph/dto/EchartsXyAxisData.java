package cn.renlm.graph.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 二维数据封装（Echarts）
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
public class EchartsXyAxisData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String xAxis;

	private BigDecimal yAxis;

}