package cn.renlm.graph.modular.graph.service.impl;

import java.util.Date;
import java.util.List;
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
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
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
	public Page<Graph> findPage(Page<Graph> page, GraphDto form) {
		page.setOptimizeCountSql(false);
		return this.page(page, Wrappers.<Graph>lambdaQuery().func(wrapper -> {
			wrapper.select(Graph.class, field -> !StrUtil.equals(field.getColumn(), "xml"));
			wrapper.eq(Graph::getDeleted, false);
			wrapper.orderByDesc(Graph::getUpdatedAt);
			wrapper.orderByDesc(Graph::getId);
			if (StrUtil.isNotBlank(form.getCategoryCode())) {
				wrapper.eq(Graph::getCategoryCode, form.getCategoryCode());
			}
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(item -> {
					item.like(Graph::getName, form.getKeywords());
				});
			}
		}));
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
		markdown.setGraphId(form.getId());
		iMarkdownService.ajaxSave(user, markdown);
		return Result.success(form);
	}
}
