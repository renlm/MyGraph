package cn.renlm.graph.modular.doc.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectDto;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.mapper.DocProjectMapper;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 文档项目 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
@Service
public class DocProjectServiceImpl extends ServiceImpl<DocProjectMapper, DocProject> implements IDocProjectService {

	@Override
	public Page<DocProject> findPage(Page<DocProject> page, User user, DocProjectDto form) {
		return this.page(page, Wrappers.<DocProject>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocProject::getCreatorUserId, user.getUserId());
			wrapper.eq(DocProject::getDeleted, false);
			wrapper.orderByDesc(DocProject::getUpdatedAt);
			wrapper.orderByDesc(DocProject::getId);
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(item -> {
					item.or().like(DocProject::getProjectName, form.getKeywords());
				});
			}
		}));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<DocProjectDto> ajaxSave(User user, DocProjectDto form) {
		if (StrUtil.isBlank(form.getUuid())) {
			form.setUuid(IdUtil.simpleUUID().toUpperCase());
			form.setCreatedAt(new Date());
			form.setCreatorUserId(user.getUserId());
			form.setCreatorNickname(user.getNickname());
			form.setUpdatedAt(form.getCreatedAt());
			form.setDeleted(false);
		} else {
			DocProject entity = this.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, form.getUuid()));
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delByUuids(User user, String... uuids) {
		this.update(Wrappers.<DocProject>lambdaUpdate().func(wrapper -> {
			wrapper.set(DocProject::getDeleted, true);
			wrapper.set(DocProject::getUpdatedAt, new Date());
			wrapper.set(DocProject::getUpdatorUserId, user.getUserId());
			wrapper.set(DocProject::getUpdatorNickname, user.getNickname());
			wrapper.eq(DocProject::getDeleted, false);
			wrapper.in(DocProject::getUuid, CollUtil.newArrayList(uuids));
		}));
	}
}
