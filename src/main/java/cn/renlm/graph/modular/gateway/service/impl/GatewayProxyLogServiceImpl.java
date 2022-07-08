package cn.renlm.graph.modular.gateway.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;
import cn.renlm.graph.modular.gateway.mapper.GatewayProxyLogMapper;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyLogService;

/**
 * <p>
 * 网关代理日志 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-07-08
 */
@Service
public class GatewayProxyLogServiceImpl extends ServiceImpl<GatewayProxyLogMapper, GatewayProxyLog> implements IGatewayProxyLogService {

	@Override
	public void recordLog(GatewayProxyLog gatewayProxyLog) {
		
	}
}
