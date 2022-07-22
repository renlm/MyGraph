package cn.renlm.graph.modular.gateway.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.renlm.graph.dto.EchartsXyAxis;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;

/**
 * <p>
 * 网关代理日志 Mapper 接口
 * </p>
 *
 * @author Renlm
 * @since 2022-07-08
 */
public interface GatewayProxyLogMapper extends BaseMapper<GatewayProxyLog> {

	/**
	 * 统计数据（访问用户数）
	 * 
	 * @param proxyConfigId
	 * @return
	 */
	List<EchartsXyAxis> getUvStatisticalData(Long proxyConfigId);

	/**
	 * 统计数据（页面访问量）
	 * 
	 * @param proxyConfigId
	 * @return
	 */
	List<EchartsXyAxis> getPvStatisticalData(Long proxyConfigId);

}
