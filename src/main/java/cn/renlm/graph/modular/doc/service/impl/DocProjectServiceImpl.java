package cn.renlm.graph.modular.doc.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import cn.renlm.graph.modular.doc.entity.DocProjectTag;
import cn.renlm.graph.modular.doc.mapper.DocProjectMapper;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.modular.doc.service.IDocProjectTagService;
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

	@Autowired
	private IDocProjectTagService iDocProjectTagService;

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
		// 处理标签
		List<String> tagNames = CollUtil.newArrayList();
		if (StrUtil.isNotBlank(form.getTags())) {
			form.setTags(StrUtil.replace(form.getTags(), "，", StrUtil.COMMA));
			List<String> list = StrUtil.splitTrim(form.getTags(), StrUtil.COMMA);
			if (CollUtil.isNotEmpty(list)) {
				CollUtil.removeBlank(list);
				tagNames.addAll(list);
			}
		}
		form.setTags(StrUtil.join(StrUtil.COMMA, tagNames));
		if (StrUtil.isBlank(form.getUuid())) {
			// 插入项目
			form.setUuid(IdUtil.simpleUUID().toUpperCase());
			form.setCreatedAt(new Date());
			form.setCreatorUserId(user.getUserId());
			form.setCreatorNickname(user.getNickname());
			form.setUpdatedAt(form.getCreatedAt());
			form.setDeleted(false);
		} else {
			// 更新项目
			DocProject entity = this.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, form.getUuid()));
			form.setId(entity.getId());
			form.setCreatedAt(entity.getCreatedAt());
			form.setCreatorUserId(entity.getCreatorUserId());
			form.setCreatorNickname(entity.getCreatorNickname());
			form.setUpdatedAt(new Date());
			form.setUpdatorUserId(user.getUserId());
			form.setUpdatorNickname(user.getNickname());
			form.setDeleted(entity.getDeleted());
			// 删除标签
			iDocProjectTagService.update(Wrappers.<DocProjectTag>lambdaUpdate().func(wrapper -> {
				wrapper.set(DocProjectTag::getDeleted, true);
				wrapper.set(DocProjectTag::getUpdatedAt, new Date());
				wrapper.set(DocProjectTag::getUpdatorUserId, user.getUserId());
				wrapper.set(DocProjectTag::getUpdatorNickname, user.getNickname());
				wrapper.eq(DocProjectTag::getDeleted, false);
				wrapper.eq(DocProjectTag::getDocProjectId, entity.getId());
			}));
		}
		// 保存项目
		this.saveOrUpdate(form);
		// 保存标签
		List<DocProjectTag> tags = CollUtil.newArrayList();
		if (CollUtil.isNotEmpty(tagNames)) {
			tagNames.forEach(tagName -> {
				DocProjectTag tag = new DocProjectTag();
				tag.setDocProjectId(form.getId());
				tag.setUuid(IdUtil.simpleUUID().toUpperCase());
				tag.setTagName(tagName);
				tag.setCreatedAt(new Date());
				tag.setCreatorUserId(user.getUserId());
				tag.setCreatorNickname(user.getNickname());
				tag.setUpdatedAt(form.getCreatedAt());
				tag.setDeleted(false);
				tags.add(tag);
			});
			if (CollUtil.isNotEmpty(tags)) {
				iDocProjectTagService.saveBatch(tags);
			}
		}
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
