package cn.renlm.graph.modular.gateway.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.modular.gateway.mapper.GatewayProxyConfigMapper;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;

/**
 * <p>
 * 网关代理配置 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-07-01
 */
@Service
public class GatewayProxyConfigServiceImpl extends ServiceImpl<GatewayProxyConfigMapper, GatewayProxyConfig> implements IGatewayProxyConfigService {

}
