package cn.renlm.mygraph.modular.oauth2.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.modular.oauth2.dto.Oauth2RegisteredClientDto;
import cn.renlm.mygraph.modular.oauth2.entity.Oauth2RegisteredClient;
import cn.renlm.mygraph.modular.oauth2.mapper.Oauth2RegisteredClientMapper;
import cn.renlm.mygraph.modular.oauth2.service.IOauth2RegisteredClientService;

/**
 * <p>
 * Oauth2.0 注册客户端 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2023-01-07
 */
@Service
public class Oauth2RegisteredClientServiceImpl extends ServiceImpl<Oauth2RegisteredClientMapper, Oauth2RegisteredClient>
		implements IOauth2RegisteredClientService {

	@Override
	public Page<Oauth2RegisteredClient> findPage(Page<Oauth2RegisteredClient> page, Oauth2RegisteredClientDto form) {
		return this.page(page, Wrappers.<Oauth2RegisteredClient>lambdaQuery().func(wrapper -> {
			wrapper.orderByDesc(Oauth2RegisteredClient::getClientIdIssuedAt);
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(it -> {
					it.or().like(Oauth2RegisteredClient::getClientId, form.getKeywords());
					it.or().like(Oauth2RegisteredClient::getClientName, form.getKeywords());
					it.or().like(Oauth2RegisteredClient::getRedirectUris, form.getKeywords());
				});
			}
		}));
	}

}
