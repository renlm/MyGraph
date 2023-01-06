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
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryCollectDto;
import cn.renlm.graph.modular.doc.dto.DocProjectDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocCategoryCollect;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.mapper.DocCategoryCollectMapper;
import cn.renlm.graph.modular.doc.service.IDocCategoryCollectService;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.plugins.MyResponse.StatusCode;

/**
 * <p>
 * 文档分类-收藏 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-15
 */
@Service
public class DocCategoryCollectServiceImpl extends ServiceImpl<DocCategoryCollectMapper, DocCategoryCollect>
		implements IDocCategoryCollectService {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Override
	public boolean isCollected(User user, Long docCategoryId) {
		return this.count(Wrappers.<DocCategoryCollect>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocCategoryCollect::getDocCategoryId, docCategoryId);
			wrapper.eq(DocCategoryCollect::getMemberUserId, user.getUserId());
			wrapper.eq(DocCategoryCollect::getDeleted, false);
		})) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> optCollect(int type, User user, String docProjectUuid, String docCategoryUuid) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		if (!BooleanUtil.isFalse(docProject.getDeleted())) {
			return Result.of(StatusCode.FORBIDDEN, "项目已被删除");
		}
		DocCategory docCategory = iDocCategoryService.getOne(Wrappers.<DocCategory>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocCategory::getDocProjectId, docProject.getId());
			wrapper.eq(DocCategory::getUuid, docCategoryUuid);
		}));
		if (!BooleanUtil.isFalse(docCategory.getDeleted())) {
			return Result.of(StatusCode.FORBIDDEN, "数据已被删除");
		}
		// 取消收藏
		this.update(Wrappers.<DocCategoryCollect>lambdaUpdate().func(wrapper -> {
			wrapper.set(DocCategoryCollect::getDeleted, true);
			wrapper.set(DocCategoryCollect::getUpdatedAt, new Date());
			wrapper.eq(DocCategoryCollect::getDeleted, false);
			wrapper.eq(DocCategoryCollect::getMemberUserId, user.getUserId());
			wrapper.in(DocCategoryCollect::getDocCategoryId, docCategory.getId());
		}));
		// 添加收藏
		if (type == 1) {
			DocCategoryCollect docCategoryCollect = new DocCategoryCollect();
			docCategoryCollect.setDocProjectId(docCategory.getDocProjectId());
			docCategoryCollect.setDocCategoryId(docCategory.getId());
			docCategoryCollect.setMemberUserId(user.getUserId());
			docCategoryCollect.setCreatedAt(new Date());
			docCategoryCollect.setUpdatedAt(docCategoryCollect.getCreatedAt());
			docCategoryCollect.setDeleted(false);
			this.save(docCategoryCollect);
		}
		return Result.success();
	}

	@Override
	public Page<DocCategoryCollectDto> findPage(Page<DocCategoryCollectDto> page, User user,
			DocCategoryCollectDto form) {
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
		Page<DocCategoryCollectDto> result = this.baseMapper.findDocPage(page, user, projectIds, form);
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
