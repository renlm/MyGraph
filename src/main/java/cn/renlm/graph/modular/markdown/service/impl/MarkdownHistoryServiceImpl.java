package cn.renlm.graph.modular.markdown.service.impl;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectDto;
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

	@Override
	public Page<MarkdownHistoryDto> findDocPage(Page<MarkdownHistory> page, User user, MarkdownHistoryDto form) {
		List<DocProjectDto> allDocProjects = iDocProjectService.findAll(user);
		Page<MarkdownHistoryDto> result = new Page<>(page.getCurrent(), page.getSize());
		BeanUtil.copyProperties(page, result);
		if (CollUtil.isEmpty(allDocProjects)) {
			return result;
		}
		// 分页查询
		List<Long> projectIds = allDocProjects.stream().map(DocProjectDto::getId).collect(Collectors.toList());
		Page<MarkdownHistory> pager = this.page(page, Wrappers.<MarkdownHistory>lambdaQuery().func(wrapper -> {
			wrapper.inSql(MarkdownHistory::getMarkdownUuid, StrUtil.indexedFormat("", StrUtil.join(COMMA, projectIds)));
		}));
		// 封装结果
		List<MarkdownHistoryDto> list = pager.getRecords().stream().filter(Objects::nonNull).map(obj -> {
			MarkdownHistoryDto data = BeanUtil.copyProperties(obj, MarkdownHistoryDto.class);
			return data;
		}).collect(Collectors.toList());
		return result.setRecords(list);
	}
}
