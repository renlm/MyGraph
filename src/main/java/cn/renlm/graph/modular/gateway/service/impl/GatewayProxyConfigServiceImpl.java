package cn.renlm.graph.modular.gateway.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyConfigDto;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.modular.gateway.mapper.GatewayProxyConfigMapper;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import cn.renlm.graph.response.Result;

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
	public Page<GatewayProxyConfig> findPage(Page<GatewayProxyConfig> page, User user, GatewayProxyConfigDto form) {
		return this.page(page, Wrappers.<GatewayProxyConfig>lambdaQuery().func(wrapper -> {
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(item -> {
					item.or().like(GatewayProxyConfig::getPath, form.getKeywords());
					item.or().like(GatewayProxyConfig::getName, form.getKeywords());
					item.or().like(GatewayProxyConfig::getOutgoingServers, form.getKeywords());
					item.or().like(GatewayProxyConfig::getRemark, form.getKeywords());
				});
			}
			wrapper.orderByDesc(GatewayProxyConfig::getUpdatedAt);
			wrapper.orderByDesc(GatewayProxyConfig::getId);
		}));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<GatewayProxyConfigDto> ajaxSave(User user, GatewayProxyConfigDto form) {
		long cnt = this.count(Wrappers.<GatewayProxyConfig>lambdaQuery().func(wrapper -> {
			if (StrUtil.isNotBlank(form.getUuid())) {
				wrapper.ne(GatewayProxyConfig::getUuid, form.getUuid());
			}
			wrapper.eq(GatewayProxyConfig::getPath, form.getPath());
		}));
		if (cnt > 0) {
			return Result.error("代理路径已被占用，请重新填写");
		}
		form.setEnabled(ObjectUtil.defaultIfNull(form.getEnabled(), true));
		form.setConnectionTimeout(ObjectUtil.defaultIfNull(form.getConnectionTimeout(), 1));
		form.setReadTimeout(ObjectUtil.defaultIfNull(form.getReadTimeout(), 600));
		form.setWriteTimeout(ObjectUtil.defaultIfNull(form.getWriteTimeout(), 600));
		form.setLimitForSecond(ObjectUtil.defaultIfNull(form.getLimitForSecond(), 10000));
		if (StrUtil.isBlank(form.getUuid())) {
			form.setUuid(IdUtil.simpleUUID().toUpperCase());
			form.setAccessKey(IdUtil.simpleUUID().toUpperCase());
			form.setSecretKey(ObjectUtil.defaultIfBlank(form.getSecretKey(), IdUtil.simpleUUID().toUpperCase()));
			form.setCreatedAt(new Date());
			form.setCreatorUserId(user.getUserId());
			form.setCreatorNickname(user.getNickname());
			form.setUpdatedAt(form.getCreatedAt());
		} else {
			GatewayProxyConfig entity = this
					.getOne(Wrappers.<GatewayProxyConfig>lambdaQuery().eq(GatewayProxyConfig::getUuid, form.getUuid()));
			form.setId(entity.getId());
			form.setAccessKey(entity.getAccessKey());
			form.setSecretKey(ObjectUtil.defaultIfBlank(form.getSecretKey(), entity.getSecretKey()));
			form.setCreatedAt(entity.getCreatedAt());
			form.setCreatorUserId(entity.getCreatorUserId());
			form.setCreatorNickname(entity.getCreatorNickname());
			form.setUpdatedAt(new Date());
			form.setUpdatorUserId(user.getUserId());
			form.setUpdatorNickname(user.getNickname());
		}
		this.saveOrUpdate(form);
		return Result.success(form);
	}
}
