package cn.renlm.graph.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.GraphDto;
import cn.renlm.graph.entity.Graph;
import cn.renlm.graph.mapper.GraphMapper;
import cn.renlm.graph.service.IGraphService;

/**
 * <p>
 * 图形设计 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-02-26
 */
@Service
public class GraphServiceImpl extends ServiceImpl<GraphMapper, Graph> implements IGraphService {

	@Override
	public Page<Graph> findPage(Page<Graph> page, GraphDto form) {
		page.setOptimizeCountSql(false);
		return this.page(page, Wrappers.<Graph>lambdaQuery().func(wrapper -> {
			wrapper.select(Graph.class, field -> !StrUtil.equals(field.getColumn(), "xml"));
			wrapper.eq(Graph::getIsPublic, true);
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
}
