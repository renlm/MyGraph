package cn.renlm.graph.modular.gateway.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 网关代理-统计数据
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
public class GatewayStatisticalDataDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 时间轴
	 */
	private List<String> dayXAxis = new ArrayList<>();

	/**
	 * 访问用户数
	 */
	private List<BigDecimal> uvYAxis = new ArrayList<>();

	/**
	 * 页面访问量
	 */
	private List<BigDecimal> pvYAxis = new ArrayList<>();

}