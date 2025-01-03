package cn.renlm.mygraph.modular.doc.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.doc.dto.DocProjectDto;
import cn.renlm.mygraph.modular.doc.entity.DocProject;
import cn.renlm.mygraph.modular.doc.entity.DocProjectMember;
import cn.renlm.mygraph.modular.doc.entity.DocProjectTag;
import cn.renlm.mygraph.modular.doc.mapper.DocProjectMapper;
import cn.renlm.mygraph.modular.doc.service.IDocProjectMemberService;
import cn.renlm.mygraph.modular.doc.service.IDocProjectService;
import cn.renlm.mygraph.modular.doc.service.IDocProjectTagService;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.plugins.MyResponse.StatusCode;

/**
 * <p>
 * 文档项目 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-06-13
 */
@Service
public class DocProjectServiceImpl extends ServiceImpl<DocProjectMapper, DocProject> implements IDocProjectService {

	@Autowired
	private IDocProjectTagService iDocProjectTagService;

	@Autowired
	private IDocProjectMemberService iDocProjectMemberService;

	@Override
	public Integer findRole(User user, Long docProjectId) {
		List<DocProjectMember> members = iDocProjectMemberService
				.list(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
					wrapper.eq(DocProjectMember::getDocProjectId, docProjectId);
					wrapper.eq(DocProjectMember::getMemberUserId, user.getUserId());
					wrapper.eq(DocProjectMember::getDeleted, false);
				}));
		OptionalInt max = members.stream().filter(Objects::nonNull).mapToInt(DocProjectMember::getRole).max();
		if (max.isPresent()) {
			return max.getAsInt();
		} else {
			return null;
		}
	}

	@Override
	public List<DocProjectDto> findAll(User user) {
		Page<DocProjectDto> page = new Page<>(1, Integer.MAX_VALUE);
		DocProjectDto form = new DocProjectDto();
		return this.findPage(page, user, form).getRecords();
	}

	@Override
	public Page<DocProjectDto> findPage(Page<DocProjectDto> page, User user, DocProjectDto form) {
		page.setOptimizeCountSql(false);
		return this.baseMapper.findPage(page, user, form);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<DocProjectDto> ajaxSave(User user, DocProjectDto form) {
		boolean isInsert = StrUtil.isBlank(form.getUuid());
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
		if (isInsert) {
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
			long members = iDocProjectMemberService.count(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
				wrapper.eq(DocProjectMember::getRole, 3);
				wrapper.eq(DocProjectMember::getDocProjectId, entity.getId());
				wrapper.eq(DocProjectMember::getMemberUserId, user.getUserId());
				wrapper.eq(DocProjectMember::getDeleted, false);
			}));
			if (members == 0) {
				return Result.of(StatusCode.FORBIDDEN, "非管理员，无操作权限");
			}
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
				tag.setTagName(tagName);
				tag.setCreatedAt(new Date());
				tag.setUpdatedAt(form.getCreatedAt());
				tag.setDeleted(false);
				tags.add(tag);
			});
			if (CollUtil.isNotEmpty(tags)) {
				iDocProjectTagService.saveBatch(tags);
			}
		}
		// 设置管理员
		if (isInsert) {
			DocProjectMember member = new DocProjectMember();
			member.setDocProjectId(form.getId());
			member.setMemberUserId(user.getUserId());
			member.setRole(3);
			member.setCreatedAt(new Date());
			member.setUpdatedAt(form.getCreatedAt());
			member.setDeleted(false);
			iDocProjectMemberService.save(member);
		}
		return Result.success(form);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> delByUuid(User user, String uuid) {
		DocProject entity = this.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, uuid));
		long members = iDocProjectMemberService.count(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocProjectMember::getRole, 3);
			wrapper.eq(DocProjectMember::getDocProjectId, entity.getId());
			wrapper.eq(DocProjectMember::getMemberUserId, user.getUserId());
			wrapper.eq(DocProjectMember::getDeleted, false);
		}));
		if (members == 0) {
			return Result.of(StatusCode.FORBIDDEN, "非管理员，无操作权限");
		}
		this.update(Wrappers.<DocProject>lambdaUpdate().func(wrapper -> {
			wrapper.set(DocProject::getDeleted, true);
			wrapper.set(DocProject::getUpdatedAt, new Date());
			wrapper.set(DocProject::getUpdatorUserId, user.getUserId());
			wrapper.set(DocProject::getUpdatorNickname, user.getNickname());
			wrapper.eq(DocProject::getDeleted, false);
			wrapper.eq(DocProject::getUuid, uuid);
		}));
		return Result.success();
	}
}
