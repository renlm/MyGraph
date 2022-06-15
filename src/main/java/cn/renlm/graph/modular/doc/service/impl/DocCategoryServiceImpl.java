package cn.renlm.graph.modular.doc.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.entity.DocProjectMember;
import cn.renlm.graph.modular.doc.mapper.DocCategoryMapper;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectMemberService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.TreeExtraUtil;

/**
 * <p>
 * 文档分类 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
@Service
public class DocCategoryServiceImpl extends ServiceImpl<DocCategoryMapper, DocCategory> implements IDocCategoryService {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocProjectMemberService iDocProjectMemberService;

	@Override
	public List<DocCategory> findListByPid(String docProjectUuid, Long pid) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		return this.list(Wrappers.<DocCategory>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocCategory::getDocProjectId, docProject.getId());
			wrapper.eq(DocCategory::getDeleted, false);
			if (pid == null) {
				wrapper.isNull(DocCategory::getPid);
				wrapper.eq(DocCategory::getLevel, 1);
			} else {
				wrapper.eq(DocCategory::getPid, pid);
			}
			wrapper.orderByAsc(DocCategory::getSort);
			wrapper.orderByAsc(DocCategory::getId);
		}));
	}

	@Override
	public List<DocCategory> findFathers(String docProjectUuid, Long id) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		if (id == null) {
			return CollUtil.newArrayList();
		}
		DocCategory docCategory = this.getOne(Wrappers.<DocCategory>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocCategory::getDocProjectId, docProject.getId());
			wrapper.eq(DocCategory::getId, id);
		}));
		List<DocCategory> list = CollUtil.newArrayList(docCategory);
		int level = docCategory.getLevel();
		while (--level > 0) {
			list.add(this.getById(CollUtil.getLast(list).getPid()));
		}
		return CollUtil.reverse(list);
	}

	@Override
	public List<Tree<Long>> getTree(String docProjectUuid, boolean root, Long pid) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		List<DocCategory> list = this.list(Wrappers.<DocCategory>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocCategory::getDocProjectId, docProject.getId());
			wrapper.eq(DocCategory::getDeleted, false);
		}));
		if (CollUtil.isEmpty(list)) {
			return CollUtil.newArrayList();
		}
		Tree<Long> top = new Tree<>();
		List<Tree<Long>> tree = TreeUtil.build(list, pid, (object, treeNode) -> {
			BeanUtil.copyProperties(object, treeNode);
			treeNode.setId(object.getId());
			treeNode.setName(object.getText());
			treeNode.setWeight(object.getSort());
			treeNode.setParentId(object.getPid());
			if (BooleanUtil.isFalse(root)) {
				return;
			}
			if (pid == null) {
				return;
			}
			if (NumberUtil.equals(pid, object.getId())) {
				BeanUtil.copyProperties(treeNode, top);
			}
		});
		if (ObjectUtil.isNotEmpty(top)) {
			top.setChildren(tree);
			return TreeExtraUtil.resetLevel(CollUtil.newArrayList(top), 1);
		} else {
			return TreeExtraUtil.resetLevel(tree, 1);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<DocCategory> ajaxSave(User user, String docProjectUuid, DocCategory docCategory) {
		boolean isInsert = StrUtil.isBlank(docCategory.getUuid());
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		docCategory.setDocProjectId(docProject.getId());
		long members = iDocProjectMemberService.count(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
			wrapper.in(DocProjectMember::getRole, 2, 3);
			wrapper.eq(DocProjectMember::getDocProjectId, docProject.getId());
			wrapper.eq(DocProjectMember::getMemberUserId, user.getUserId());
			wrapper.eq(DocProjectMember::getDeleted, false);
		}));
		if (members == 0) {
			return Result.of(HttpStatus.FORBIDDEN, "您没有操作权限");
		}
		if (docCategory.getPid() == null) {
			docCategory.setLevel(1);
		} else {
			DocCategory parent = this.getById(docCategory.getPid());
			parent.setState(TreeState.closed.name());
			docCategory.setLevel(parent.getLevel() + 1);
			List<DocCategory> fathers = this.findFathers(docProjectUuid, parent.getId());
			List<Long> fatherIds = fathers.stream().map(DocCategory::getId).collect(Collectors.toList());
			if (fatherIds.contains(docCategory.getId())) {
				return Result.error("不能选择自身或子节点作为父级");
			} else {
				parent.setUpdatedAt(new Date());
				this.updateById(parent);
			}
		}
		if (isInsert) {
			// 插入
			docCategory.setUuid(IdUtil.simpleUUID().toUpperCase());
			docCategory.setCreatedAt(new Date());
			docCategory.setCreatorUserId(user.getUserId());
			docCategory.setCreatorNickname(user.getNickname());
			docCategory.setUpdatedAt(docCategory.getCreatedAt());
			docCategory.setDeleted(false);
		} else {
			// 更新
			DocCategory entity = this
					.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, docCategory.getUuid()));
			docCategory.setId(entity.getId());
			docCategory.setCreatedAt(entity.getCreatedAt());
			docCategory.setCreatorUserId(entity.getCreatorUserId());
			docCategory.setCreatorNickname(entity.getCreatorNickname());
			docCategory.setUpdatedAt(new Date());
			docCategory.setUpdatorUserId(user.getUserId());
			docCategory.setUpdatorNickname(user.getNickname());
			docCategory.setDeleted(entity.getDeleted());
		}
		this.saveOrUpdate(docCategory);
		return Result.success(docCategory);
	}
}
