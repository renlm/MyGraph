package cn.renlm.graph.modular.gateway.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyLogDto;
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
public class GatewayProxyLogServiceImpl extends ServiceImpl<GatewayProxyLogMapper, GatewayProxyLog>
		implements IGatewayProxyLogService {

	@Override
	public Page<GatewayProxyLog> findPage(Page<GatewayProxyLog> page, User user, GatewayProxyLogDto form) {
		return this.page(page, Wrappers.<GatewayProxyLog>lambdaQuery().func(wrapper -> {
			wrapper.orderByDesc(GatewayProxyLog::getId);
		}));
	}
}
