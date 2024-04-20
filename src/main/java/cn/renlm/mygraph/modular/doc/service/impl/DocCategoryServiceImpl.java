package cn.renlm.mygraph.modular.doc.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.common.TreeState;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.doc.entity.DocCategory;
import cn.renlm.mygraph.modular.doc.entity.DocProject;
import cn.renlm.mygraph.modular.doc.mapper.DocCategoryMapper;
import cn.renlm.mygraph.modular.doc.service.IDocCategoryService;
import cn.renlm.mygraph.modular.doc.service.IDocProjectService;
import cn.renlm.mygraph.modular.markdown.entity.Markdown;
import cn.renlm.mygraph.modular.markdown.entity.MarkdownHistory;
import cn.renlm.mygraph.modular.markdown.service.IMarkdownHistoryService;
import cn.renlm.mygraph.modular.markdown.service.IMarkdownService;
import cn.renlm.mygraph.util.TreeExtraUtil;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.plugins.MyResponse.StatusCode;

/**
 * <p>
 * 文档分类 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-06-13
 */
@Service
public class DocCategoryServiceImpl extends ServiceImpl<DocCategoryMapper, DocCategory> implements IDocCategoryService {

	@Autowired
	private IMarkdownService iMarkdownService;

	@Autowired
	private IMarkdownHistoryService iMarkdownHistoryService;

	@Autowired
	private IDocProjectService iDocProjectService;

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
		if (StrUtil.isBlank(docProjectUuid)) {
			return CollUtil.newArrayList();
		}
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
		final List<DocCategory> fathers = CollUtil.newArrayList();
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
		Integer role = iDocProjectService.findRole(user, docCategory.getDocProjectId());
		if (role == null || !ArrayUtil.contains(new Integer[] { 2, 3 }, role.intValue())) {
			return Result.of(StatusCode.FORBIDDEN, "您没有操作权限");
		}
		if (docCategory.getPid() == null) {
			docCategory.setFullname(docCategory.getText());
			docCategory.setLevel(1);
		} else {
			DocCategory parent = this.getById(docCategory.getPid());
			parent.setState(TreeState.closed.name());
			docCategory.setLevel(parent.getLevel() + 1);
			if (CollUtil.isEmpty(fathers)) {
				fathers.addAll(this.findFathers(docProjectUuid, parent.getId()));
			}
			List<Long> fatherIds = fathers.stream().map(DocCategory::getId).collect(Collectors.toList());
			List<String> fathersName = fathers.stream().map(DocCategory::getText).collect(Collectors.toList());
			docCategory.setFullname(StrUtil.join(StrUtil.SLASH, fathersName, docCategory.getText()));
			if (fatherIds.contains(docCategory.getId())) {
				return Result.error("不能选择自身或子节点作为父级");
			} else {
				parent.setUpdatedAt(new Date());
				this.updateById(parent);
			}
		}
		// 排序
		if (docCategory.getSort() == null) {
			List<DocCategory> childs = this.findListByPid(docProjectUuid, docCategory.getPid());
			OptionalInt max = childs.stream().filter(Objects::nonNull).mapToInt(DocCategory::getSort).max();
			if (max.isPresent()) {
				docCategory.setSort(max.getAsInt() + 1);
			}
		}
		docCategory.setSort(ObjectUtil.defaultIfNull(docCategory.getSort(), 1));
		this.saveOrUpdate(docCategory);
		// 关联文档处理
		String docCategoryName = docCategory.getText();
		String projectName = docProject.getProjectName();
		if (isInsert) {
			// 初始化文档
			Markdown markdown = new Markdown();
			markdown.setUuid(docCategory.getUuid());
			if (docCategory.getPid() == null) {
				markdown.setName(StrUtil.join(StrUtil.SLASH, projectName, docCategoryName));
			} else {
				if (CollUtil.isEmpty(fathers)) {
					fathers.addAll(this.findFathers(docProjectUuid, docCategory.getPid()));
				}
				String fathersName = fathers.stream().map(DocCategory::getText)
						.collect(Collectors.joining(StrUtil.SLASH));
				markdown.setName(StrUtil.join(StrUtil.SLASH, projectName, fathersName, docCategoryName));
			}
			markdown.setVersion(1);
			markdown.setCreatedAt(new Date());
			markdown.setCreatorUserId(user.getUserId());
			markdown.setCreatorNickname(user.getNickname());
			markdown.setUpdatedAt(markdown.getCreatedAt());
			markdown.setDeleted(false);
			iMarkdownService.save(markdown);
			// 历史记录
			MarkdownHistory history = BeanUtil.copyProperties(markdown, MarkdownHistory.class);
			history.setChangeLabel(isInsert ? "新增" : "修改");
			history.setOperateAt(new Date());
			history.setOperatorUserId(user.getUserId());
			history.setOperatorNickname(user.getNickname());
			history.setMarkdownId(markdown.getId());
			history.setMarkdownUuid(markdown.getUuid());
			iMarkdownHistoryService.save(history);
		} else {
			Map<String, Object[]> map = new LinkedHashMap<>();
			List<Tree<Long>> roots = this.getTree(docProjectUuid, true, null);
			TreeExtraUtil.resetLevel(roots, 1);
			roots.forEach(root -> {
				Tree<Long> node = TreeUtil.getNode(root, docCategory.getId());
				if (ObjectUtil.isNotEmpty(node)) {
					List<Tree<Long>> childs = TreeExtraUtil.getAllNodes(CollUtil.newArrayList(node));
					childs.forEach(child -> {
						DocCategory dc = BeanUtil.copyProperties(child, DocCategory.class);
						List<CharSequence> parents = TreeUtil.getParentsName(child, true);
						CollUtil.removeBlank(parents);
						CollUtil.reverse(parents);
						String parentsName = StrUtil.join(StrUtil.SLASH, parents);
						map.put(dc.getUuid(), new Object[] { parentsName, dc.getLevel() });
					});
				}
			});
			map.forEach((docCategoryUuid, arr) -> {
				String parentsName = Convert.toStr(arr[0]);
				int level = Convert.toInt(arr[1]);
				iMarkdownService.update(Wrappers.<Markdown>lambdaUpdate().func(wrapper -> {
					wrapper.set(Markdown::getName, StrUtil.join(StrUtil.SLASH, projectName, parentsName));
					wrapper.set(Markdown::getUpdatedAt, new Date());
					wrapper.eq(Markdown::getUuid, docCategoryUuid);
				}));
				this.update(Wrappers.<DocCategory>lambdaUpdate().func(wrapper -> {
					wrapper.set(DocCategory::getFullname, parentsName);
					wrapper.set(DocCategory::getLevel, level);
					wrapper.set(DocCategory::getUpdatedAt, new Date());
					wrapper.eq(DocCategory::getUuid, docCategoryUuid);
				}));
			});
		}
		return Result.success(docCategory);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> delByUuid(User user, String uuid) {
		DocCategory entity = this.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, uuid));
		DocProject docProject = iDocProjectService.getById(entity.getDocProjectId());
		Integer role = iDocProjectService.findRole(user, entity.getDocProjectId());
		if (role == null || !ArrayUtil.contains(new Integer[] { 2, 3 }, role.intValue())) {
			return Result.of(StatusCode.FORBIDDEN, "您没有操作权限");
		}
		// 获取全部子节点
		List<Tree<Long>> tree = this.getTree(docProject.getUuid(), true, entity.getId());
		List<Long> ids = CollUtil.newArrayList();
		List<String> uuids = CollUtil.newArrayList();
		TreeExtraUtil.getAllNodes(tree).stream().map(obj -> {
			DocCategory node = BeanUtil.copyProperties(obj, DocCategory.class);
			ids.add(node.getId());
			uuids.add(node.getUuid());
			return node;
		}).collect(Collectors.toList());
		// 删除分类
		Date time = new Date();
		this.update(Wrappers.<DocCategory>lambdaUpdate().func(wrapper -> {
			wrapper.set(DocCategory::getDeleted, true);
			wrapper.set(DocCategory::getUpdatedAt, time);
			wrapper.set(DocCategory::getUpdatorUserId, user.getUserId());
			wrapper.set(DocCategory::getUpdatorNickname, user.getNickname());
			wrapper.eq(DocCategory::getDeleted, false);
			wrapper.in(DocCategory::getId, ids);
		}));
		// 获取文档
		List<MarkdownHistory> histories = CollUtil.newArrayList();
		List<Markdown> markdowns = iMarkdownService.list(Wrappers.<Markdown>lambdaQuery().func(wrapper -> {
			wrapper.eq(Markdown::getDeleted, false);
			wrapper.in(Markdown::getUuid, uuids);
		}));
		markdowns.forEach(md -> {
			md.setVersion(md.getVersion() + 1);
			md.setDeleted(true);
			md.setUpdatedAt(time);
			md.setUpdatorUserId(user.getUserId());
			md.setUpdatorNickname(user.getNickname());
			// 历史记录
			MarkdownHistory history = BeanUtil.copyProperties(md, MarkdownHistory.class);
			history.setChangeLabel("删除");
			history.setOperateAt(new Date());
			history.setOperatorUserId(user.getUserId());
			history.setOperatorNickname(user.getNickname());
			history.setMarkdownId(md.getId());
			history.setMarkdownUuid(md.getUuid());
			histories.add(history);
		});
		// 删除文档
		iMarkdownService.updateBatchById(markdowns);
		// 保存历史记录
		iMarkdownHistoryService.saveBatch(histories);
		return Result.success();
	}
}
