package cn.renlm.graph.modular.graph.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Mxgraph;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.graph.dto.GraphDto;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.mapper.GraphMapper;
import cn.renlm.graph.modular.graph.service.IGraphService;
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

	@Override
	public Page<Graph> findPage(Page<Graph> page, GraphDto form) {
		page.setOptimizeCountSql(false);
		return this.page(page, Wrappers.<Graph>lambdaQuery().func(wrapper -> {
			wrapper.select(Graph.class, field -> !StrUtil.equals(field.getColumn(), "xml"));
			// 我的作品
			if (StrUtil.isNotBlank(form.getCreatorUserId())) {
				wrapper.eq(Graph::getCreatorUserId, form.getCreatorUserId());
			}
			// 公共图库
			else {
				wrapper.eq(Graph::getIsPublic, true);
			}
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
	public Result<GraphDto> mineAjaxSave(User user, GraphDto form) {
		form.setCategoryName(Mxgraph.valueOf(form.getCategoryCode()).getText());
		if (StrUtil.isBlank(form.getUuid())) {
			form.setUuid(IdUtil.simpleUUID().toUpperCase());
			GraphDto.fillDefault(form);
			form.setCreatedAt(new Date());
			form.setCreatorUserId(user.getUserId());
			form.setCreatorNickname(user.getNickname());
			form.setUpdatedAt(form.getCreatedAt());
			form.setDeleted(false);
		} else {
			Graph entity = this.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, form.getUuid()));
			form.setId(entity.getId());
			form.setCover(entity.getCover());
			form.setZoom(entity.getZoom());
			form.setDx(entity.getDx());
			form.setDy(entity.getDy());
			form.setGridEnabled(entity.getGridEnabled());
			form.setGridSize(entity.getGridSize());
			form.setGridColor(entity.getGridColor());
			form.setPageVisible(entity.getPageVisible());
			form.setBackground(entity.getBackground());
			form.setConnectionArrowsEnabled(entity.getConnectionArrowsEnabled());
			form.setConnectable(entity.getConnectable());
			form.setGuidesEnabled(entity.getGuidesEnabled());
			form.setXml(entity.getXml());
			form.setCreatedAt(entity.getCreatedAt());
			form.setCreatorUserId(entity.getCreatorUserId());
			form.setCreatorNickname(entity.getCreatorNickname());
			form.setUpdatedAt(new Date());
			form.setUpdatorUserId(user.getUserId());
			form.setUpdatorNickname(user.getNickname());
			form.setDeleted(entity.getDeleted());
		}
		this.saveOrUpdate(form);
		return Result.success(form);
	}
}
