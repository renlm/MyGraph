package cn.renlm.graph.modular.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyLogDto;
import cn.renlm.graph.modular.gateway.dto.GatewayStatisticalDataDto;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;
import cn.renlm.graph.modular.gateway.mapper.GatewayProxyLogMapper;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
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
public class GatewayProxyLogServiceImpl extends ServiceImpl<GatewayProxyLogMapper, GatewayProxyLog>
		implements IGatewayProxyLogService {

	@Autowired
	private IGatewayProxyConfigService iGatewayProxyConfigService;

	@Override
	public Page<GatewayProxyLog> findPage(Page<GatewayProxyLog> page, User user, GatewayProxyLogDto form) {
		GatewayProxyConfig proxyConfig = iGatewayProxyConfigService.getOne(
				Wrappers.<GatewayProxyConfig>lambdaQuery().eq(GatewayProxyConfig::getUuid, form.getProxyConfigUuid()));
		String sort = form.getSort();
		String order = form.getOrder();
		return this.page(page, Wrappers.<GatewayProxyLog>lambdaQuery().func(wrapper -> {
			if (form.getRequestTime() != null) {
				wrapper.ge(GatewayProxyLog::getRequestTime, form.getRequestTime());
			}
			if (form.getTakeTime() != null) {
				wrapper.ge(GatewayProxyLog::getTakeTime, form.getTakeTime());
			}
			if (form.getStatusCode() != null) {
				wrapper.eq(GatewayProxyLog::getStatusCode, form.getStatusCode());
			}
			wrapper.eq(GatewayProxyLog::getProxyConfigId, proxyConfig.getProxyConfigId());
			wrapper.orderBy("requestTime".equals(sort), "asc".equals(order), GatewayProxyLog::getRequestTime);
			wrapper.orderBy("takeTime".equals(sort), "asc".equals(order), GatewayProxyLog::getTakeTime);
			wrapper.orderByDesc(GatewayProxyLog::getId);
		}));
	}

	@Override
	public GatewayStatisticalDataDto getStatisticalData(String proxyConfigUuid) {
		return null;
	}
}
