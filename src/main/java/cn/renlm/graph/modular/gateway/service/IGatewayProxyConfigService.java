package cn.renlm.graph.modular.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.mkopylec.charon.configuration.CharonConfigurer;

import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;

/**
 * <p>
 * 网关代理配置 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-07-01
 */
public interface IGatewayProxyConfigService extends IService<GatewayProxyConfig> {

	/**
	 * 加载配置
	 * 
	 * @param configurer
	 * @return
	 */
	CharonConfigurer loadCofig(CharonConfigurer configurer);

}
