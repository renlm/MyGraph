package cn.renlm.graph.modular.graph.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Mxgraph;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.modular.graph.dto.GraphDto;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.entity.GraphHistory;
import cn.renlm.graph.modular.graph.mapper.GraphMapper;
import cn.renlm.graph.modular.graph.service.IGraphHistoryService;
import cn.renlm.graph.modular.graph.service.IGraphService;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.service.IMarkdownService;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 图形设计 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class GraphServiceImpl extends ServiceImpl<GraphMapper, Graph> implements IGraphService {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Autowired
	private IMarkdownService iMarkdownService;

	@Autowired
	private IGraphHistoryService iGraphHistoryService;

	@Override
	public Page<GraphDto> findPage(Page<GraphDto> page, User user, GraphDto form) {
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
		Page<GraphDto> result = this.baseMapper.findPage(page, user, projectIds, form);
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> updateBaseInfo(User user, GraphDto form) {
		Graph entity = this.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, form.getUuid()));
		entity.setCategoryCode(form.getCategoryCode());
		entity.setCategoryName(Mxgraph.valueOf(form.getCategoryCode()).getText());
		entity.setRemark(form.getRemark());
		entity.setUpdatedAt(new Date());
		entity.setUpdatorUserId(user.getUserId());
		entity.setUpdatorNickname(user.getNickname());
		this.updateById(entity);
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<Graph> saveEditor(User user, GraphDto form) {
		if (StrUtil.isBlank(form.getUuid())) {
			return Result.of(HttpStatus.BAD_REQUEST, "参数不全");
		} else {
			DocCategory docCategory = iDocCategoryService
					.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, form.getUuid()));
			if (ObjectUtil.isNotEmpty(docCategory)) {
				Integer role = iDocProjectService.findRole(user, docCategory.getDocProjectId());
				if (role == null || !ArrayUtil.contains(new Integer[] { 2, 3 }, role.intValue())) {
					return Result.of(HttpStatus.FORBIDDEN, "您没有操作权限");
				} else {
					DocProject docProject = iDocProjectService.getById(docCategory.getDocProjectId());
					List<DocCategory> fathers = iDocCategoryService.findFathers(docProject.getUuid(),
							docCategory.getId());
					String fathersName = fathers.stream().map(DocCategory::getText)
							.collect(Collectors.joining(StrUtil.SLASH));
					form.setName(StrUtil.join(StrUtil.SLASH, docProject.getProjectName(), fathersName));
				}
			}
			Graph entity = this.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, form.getUuid()));
			if (entity == null) {
				form.setUuid(form.getUuid());
				form.setVersion(1);
				GraphDto.fillDefault(form);
				form.setXml(Base64.decodeStr(form.getXml()));
				form.setCreatedAt(new Date());
				form.setCreatorUserId(user.getUserId());
				form.setCreatorNickname(user.getNickname());
				form.setUpdatedAt(form.getCreatedAt());
				form.setDeleted(false);
			} else if (!BooleanUtil.isFalse(entity.getDeleted())) {
				return Result.of(HttpStatus.FORBIDDEN, "数据已被删除");
			} else {
				if (form.getVersion() == null) {
					return Result.of(HttpStatus.BAD_REQUEST, "缺失版本号");
				} else if (form.getVersion().intValue() < entity.getVersion().intValue()) {
					return Result.of(HttpStatus.BAD_REQUEST, "版本错误，请获取最新数据");
				}
				form.setId(entity.getId());
				form.setVersion(entity.getVersion() + 1);
				GraphDto.fillDefault(form);
				form.setXml(Base64.decodeStr(form.getXml()));
				form.setCreatedAt(entity.getCreatedAt());
				form.setCreatorUserId(entity.getCreatorUserId());
				form.setCreatorNickname(entity.getCreatorNickname());
				form.setUpdatedAt(new Date());
				form.setUpdatorUserId(user.getUserId());
				form.setUpdatorNickname(user.getNickname());
				form.setDeleted(entity.getDeleted());
			}
		}
		if (StrUtil.isBlank(form.getCategoryCode())) {
			form.setCategoryCode(Mxgraph.OTHER.name());
			form.setCategoryName(Mxgraph.OTHER.getText());
		}
		boolean isInsert = form.getId() == null;
		this.saveOrUpdate(form);
		// 历史记录-图形设计
		GraphHistory history = BeanUtil.copyProperties(form, GraphHistory.class);
		history.setChangeLabel(isInsert ? "新增" : "修改");
		history.setOperateAt(new Date());
		history.setOperatorUserId(user.getUserId());
		history.setOperatorNickname(user.getNickname());
		history.setGraphId(form.getId());
		history.setGraphUuid(form.getUuid());
		iGraphHistoryService.save(history);
		// 关联文档记录
		Markdown markdown = iMarkdownService
				.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, form.getUuid()));
		markdown.setGraphUuid(form.getUuid());
		markdown.setGraphVersion(form.getVersion());
		iMarkdownService.ajaxSave(user, markdown);
		return Result.success(form);
	}
}
