package cn.renlm.graph.modular.markdown.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectDto;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.modular.markdown.dto.MarkdownHistoryDto;
import cn.renlm.graph.modular.markdown.entity.MarkdownHistory;
import cn.renlm.graph.modular.markdown.mapper.MarkdownHistoryMapper;
import cn.renlm.graph.modular.markdown.service.IMarkdownHistoryService;

/**
 * <p>
 * Markdown文档-历史记录 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
@Service
public class MarkdownHistoryServiceImpl extends ServiceImpl<MarkdownHistoryMapper, MarkdownHistory>
		implements IMarkdownHistoryService {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Override
	public Page<MarkdownHistoryDto> findDocPage(Page<MarkdownHistoryDto> page, User user, MarkdownHistoryDto form) {
		List<DocProjectDto> allDocProjects = iDocProjectService.findAll(user);
		if (CollUtil.isEmpty(allDocProjects)) {
			return page;
		}
		// 项目分类
		List<Long> projectIds = CollUtil.newArrayList();
		Map<String, List<Tree<Long>>> treeMap = new LinkedHashMap<>();
		allDocProjects.forEach(project -> {
			projectIds.add(project.getId());
			List<Tree<Long>> tree = iDocCategoryService.getTree(project.getUuid(), true, null);
			treeMap.put(project.getUuid(), tree);
		});
		// 分页数据
		Page<MarkdownHistoryDto> result = this.baseMapper.findDocPage(page, projectIds, form);
		// 处理附加信息
		result.getRecords().forEach(history -> {
			List<Tree<Long>> tree = treeMap.get(history.getDocProjectUuid());
			tree.forEach(top -> {
				Tree<Long> node = TreeUtil.getNode(top, history.getDocCategoryId());
				List<CharSequence> parents = TreeUtil.getParentsName(node, true);
				CollUtil.removeBlank(parents);
				CollUtil.reverse(parents);
				history.setParentsDocCategorName(StrUtil.join(StrUtil.SLASH, parents));
			});
		});
		return result;
	}
}
