package cn.renlm.graph.modular.gateway.service.impl;

import static cn.hutool.core.text.StrPool.COMMA;
import static cn.hutool.core.text.StrPool.SLASH;
import static cn.renlm.graph.config.GatewayConfig.proxyPath;
import static com.github.mkopylec.charon.configuration.RequestMappingConfigurer.requestMapping;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RegexRequestPathRewriterConfigurer.regexRequestPathRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestServerNameRewriterConfigurer.requestServerNameRewriter;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.mkopylec.charon.configuration.CharonConfigurer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
public class GatewayProxyConfigServiceImpl extends ServiceImpl<GatewayProxyConfigMapper, GatewayProxyConfig>
		implements IGatewayProxyConfigService {

	@Override
	public CharonConfigurer loadCofig(CharonConfigurer configurer) {
		List<GatewayProxyConfig> configs = this.list(Wrappers.<GatewayProxyConfig>lambdaQuery().func(wrapper -> {
			wrapper.eq(GatewayProxyConfig::getEnabled, true);
			wrapper.orderByAsc(GatewayProxyConfig::getId);
		}));
		configs.forEach(config -> {
			String path = config.getPath();
			while (StrUtil.startWith(path, SLASH)) {
				path = StrUtil.removePrefix(path, SLASH);
			}
			while (StrUtil.endWith(path, SLASH)) {
				path = StrUtil.removeSuffix(path, SLASH);
			}
			List<String> outgoingServers = StrUtil.splitTrim(config.getOutgoingServers(), COMMA);
			CollUtil.removeBlank(outgoingServers);
			if (StrUtil.isNotBlank(path) && CollUtil.isNotEmpty(outgoingServers)) {
				configurer.add(
						requestMapping(path)
							.pathRegex(proxyPath + path + "/.*")
							.set(requestServerNameRewriter().outgoingServers(outgoingServers))
							.set(regexRequestPathRewriter().paths("/" + path + "/(?<path>.*)", "/<path>"))
							);
			}
		});
		return configurer;
	}
}
