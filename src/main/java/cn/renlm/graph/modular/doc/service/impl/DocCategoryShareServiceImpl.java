package cn.renlm.graph.modular.doc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.dto.DocProjectDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocCategoryShare;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.mapper.DocCategoryShareMapper;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocCategoryShareService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.plugins.MyResponse.StatusCode;
import jakarta.annotation.Resource;

/**
 * <p>
 * 文档分类-分享 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-16
 */
@Service
public class DocCategoryShareServiceImpl extends ServiceImpl<DocCategoryShareMapper, DocCategoryShare>
		implements IDocCategoryShareService {

	@Resource
	private RSA rsa;

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> closeShare(User user, String uuid) {
		this.update(Wrappers.<DocCategoryShare>lambdaUpdate().func(wrapper -> {
			wrapper.set(DocCategoryShare::getDisabled, true);
			wrapper.set(DocCategoryShare::getUpdatedAt, new Date());
			wrapper.set(DocCategoryShare::getUpdatorUserId, user.getUserId());
			wrapper.set(DocCategoryShare::getUpdatorNickname, user.getUsername());
			wrapper.eq(DocCategoryShare::getUuid, uuid);
			wrapper.eq(DocCategoryShare::getCreatorUserId, user.getUserId());
			wrapper.eq(DocCategoryShare::getDeleted, false);
		}));
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> ajaxSave(User user, DocCategoryShareDto form) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, form.getDocProjectUuid()));
		if (ObjectUtil.isEmpty(docProject) || !BooleanUtil.isTrue(docProject.getIsShare())) {
			return Result.of(StatusCode.FORBIDDEN, "您没有操作权限");
		}
		if (!BooleanUtil.isFalse(docProject.getDeleted())) {
			return Result.of(StatusCode.FORBIDDEN, "项目已被删除");
		}
		DocCategory docCategory = iDocCategoryService
				.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, form.getDocCategoryUuid()));
		if (ObjectUtil.isEmpty(docCategory)) {
			return Result.of(StatusCode.FORBIDDEN, "您没有操作权限");
		}
		if (!BooleanUtil.isFalse(docCategory.getDeleted())) {
			return Result.of(StatusCode.FORBIDDEN, "数据已被删除");
		}
		Integer role = iDocProjectService.findRole(user, docCategory.getDocProjectId());
		if (NumberUtil.equals(docProject.getVisitLevel(), 1) && role == null) {
			return Result.of(StatusCode.FORBIDDEN, "您没有操作权限");
		}
		// 保存分享信息
		String password = form.getPassword();
		if (NumberUtil.equals(form.getShareType(), 1)) {
			form.setPassword(null);
		} else {
			if (StrUtil.isBlank(password)) {
				return Result.of(StatusCode.BAD_REQUEST, "访问密码缺失");
			}
			form.setPassword(rsa.encryptBase64(password, KeyType.PrivateKey));
		}
		if (!NumberUtil.equals(form.getEffectiveType(), -1)) {
			form.setDeadline(DateUtil.offsetDay(new Date(), form.getEffectiveType()));
		}
		form.setDocProjectId(docCategory.getDocProjectId());
		form.setDocCategoryId(docCategory.getId());
		form.setUuid(IdUtil.simpleUUID().toUpperCase());
		form.setCreatedAt(new Date());
		form.setCreatorUserId(user.getUserId());
		form.setCreatorNickname(user.getNickname());
		form.setUpdatedAt(form.getCreatedAt());
		form.setDisabled(false);
		form.setDeleted(false);
		this.save(form);
		return Result.success(form.getUuid());
	}

	@Override
	public Page<DocCategoryShareDto> findPage(Page<DocCategoryShareDto> page, User user, DocCategoryShareDto form) {
		List<DocProjectDto> allDocProjects = iDocProjectService.findAll(user);
		if (CollUtil.isEmpty(allDocProjects)) {
			return page;
		}
		// 项目分类
		List<Long> projectIds = CollUtil.newArrayList();
		Map<Long, List<DocCategory>> docCategoryMap = new LinkedHashMap<>();
		Map<Long, List<Tree<Long>>> treeMap = new LinkedHashMap<>();
		allDocProjects.forEach(project -> {
			projectIds.add(project.getId());
		});
		List<DocCategory> list = iDocCategoryService.list(Wrappers.<DocCategory>lambdaQuery().func(wrapper -> {
			wrapper.in(DocCategory::getDocProjectId, projectIds);
		}));
		list.forEach(docCategory -> {
			if (!docCategoryMap.containsKey(docCategory.getDocProjectId())) {
				docCategoryMap.put(docCategory.getDocProjectId(), new ArrayList<>());
			}
			docCategoryMap.get(docCategory.getDocProjectId()).add(docCategory);
		});
		docCategoryMap.forEach((docProjectId, docCategories) -> {
			List<Tree<Long>> tree = TreeUtil.build(docCategories, null, (object, treeNode) -> {
				BeanUtil.copyProperties(object, treeNode);
				treeNode.setId(object.getId());
				treeNode.setName(object.getText());
				treeNode.setWeight(object.getSort());
				treeNode.setParentId(object.getPid());
			});
			treeMap.put(docProjectId, tree);
		});
		// 分页数据
		page.setOptimizeCountSql(false);
		Page<DocCategoryShareDto> result = this.baseMapper.findDocPage(page, user, projectIds, form);
		// 处理附加信息
		result.getRecords().forEach(item -> {
			List<Tree<Long>> tree = treeMap.get(item.getDocProjectId());
			tree.forEach(top -> {
				Tree<Long> node = TreeUtil.getNode(top, item.getDocCategoryId());
				List<CharSequence> parents = TreeUtil.getParentsName(node, true);
				CollUtil.removeBlank(parents);
				CollUtil.reverse(parents);
				if (CollUtil.isNotEmpty(parents)) {
					item.setParentsCategorName(StrUtil.join(StrUtil.SLASH, parents));
				}
			});
		});
		return result;
	}
}
