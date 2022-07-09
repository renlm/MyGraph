package cn.renlm.graph.modular.gateway.dto;

import java.io.Serializable;
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
	private List<Integer> uvYAxis = new ArrayList<>();

	/**
	 * 页面访问量
	 */
	private List<Integer> pvYAxis = new ArrayList<>();

}