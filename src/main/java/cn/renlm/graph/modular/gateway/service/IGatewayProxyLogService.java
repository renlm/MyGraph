package cn.renlm.graph.modular.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;

/**
 * <p>
 * 网关代理日志 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-07-08
 */
public interface IGatewayProxyLogService extends IService<GatewayProxyLog> {

	/**
	 * 记录日志
	 * 
	 * @param gatewayProxyLog
	 */
	void recordLog(GatewayProxyLog gatewayProxyLog);

}
