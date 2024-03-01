package cn.renlm.graph.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 二维数据封装（Echarts）
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
public class EchartsXyAxis implements Serializable {

	private static final long serialVersionUID = 1L;

	private String xAxis;

	private BigDecimal yAxis;

}
