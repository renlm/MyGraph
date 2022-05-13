package cn.renlm.graph.modular.er.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldLibDto;
import cn.renlm.graph.modular.er.entity.ErFieldLib;
import cn.renlm.graph.modular.er.mapper.ErFieldLibMapper;
import cn.renlm.graph.modular.er.service.IErFieldLibService;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * ER模型-我的字段库 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-13
 */
@Service
public class ErFieldLibServiceImpl extends ServiceImpl<ErFieldLibMapper, ErFieldLib> implements IErFieldLibService {

	@Override
	public Page<ErFieldLib> findPage(Page<ErFieldLib> page, User user, ErFieldLibDto form) {
		return this.page(page, Wrappers.<ErFieldLib>lambdaQuery().func(wrapper -> {
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(item -> {
					item.or().like(ErFieldLib::getName, form.getKeywords());
					item.or().like(ErFieldLib::getComment, form.getKeywords());
				});
			}
			wrapper.eq(ErFieldLib::getCreatorUserId, user.getUserId());
			wrapper.eq(ErFieldLib::getDeleted, false);
			wrapper.orderByDesc(ErFieldLib::getUpdatedAt);
			wrapper.orderByDesc(ErFieldLib::getId);
		}));
	}

	@Override
	public Result<ErFieldLib> addFieldToLib(User user, String fieldUuid) {
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<ErFieldLibDto> ajaxSave(User user, ErFieldLibDto form) {
		if (StrUtil.isBlank(form.getUuid())) {
			form.setUuid(IdUtil.simpleUUID().toUpperCase());
			form.setCreatedAt(new Date());
			form.setCreatorUserId(user.getUserId());
			form.setCreatorNickname(user.getNickname());
			form.setUpdatedAt(form.getCreatedAt());
			form.setDeleted(false);
		} else {
			ErFieldLib entity = this.getOne(Wrappers.<ErFieldLib>lambdaQuery().eq(ErFieldLib::getUuid, form.getUuid()));
			form.setId(entity.getId());
			form.setCreatedAt(entity.getCreatedAt());
			form.setCreatorUserId(entity.getCreatorUserId());
			form.setCreatorNickname(entity.getCreatorNickname());
			form.setUpdatedAt(new Date());
			form.setUpdatorUserId(user.getUserId());
			form.setUpdatorNickname(user.getNickname());
			form.setDeleted(entity.getDeleted());
		}
		this.saveOrUpdate(form);
		return Result.success(form);
	}
}
