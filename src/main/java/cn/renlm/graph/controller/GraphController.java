package cn.renlm.graph.controller;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.GraphDto;
import cn.renlm.graph.entity.Graph;
import cn.renlm.graph.service.IGraphService;

/**
 * 图形设计
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/graph")
public class GraphController {

	@Resource
	private IGraphService iGraphService;

	/**
	 * 公共图库（页面）
	 * 
	 * @return
	 */
	@RequestMapping("/lib")
	public String list() {
		return "graph/lib";
	}

	/**
	 * 公共图库（分页）
	 * 
	 * @param request
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/page")
	public Page<Graph> publicPage(HttpServletRequest request, Page<Graph> page,
			GraphDto form) {
		return iGraphService.findPage(page, form);
	}

	/**
	 * 图形编辑
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/editor")
	public String editor(ModelMap model, String uuid) {
		Graph graph = iGraphService
				.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
		graph.setXml(Base64.encodeUrlSafe(graph.getXml()));
		model.put("graphJson", JSONUtil.toJsonStr(graph));
		return "graph/editor";
	}

	/**
	 * 图形预览
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/viewer")
	public String viewer(ModelMap model, String uuid) {
		Graph graph = iGraphService
				.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
		graph.setXml(Base64.encodeUrlSafe(graph.getXml()));
		model.put("graphJson", JSONUtil.toJsonStr(graph));
		return "graph/viewer";
	}

	/**
	 * 保存
	 * 
	 * @param request
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/saveEditor")
	public Result saveEditor(HttpServletRequest request, GraphDto form) {
		try {
			Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery()
					.eq(Graph::getUuid, form.getUuid()));
			graph.setZoom(ObjectUtil.defaultIfNull(form.getZoom(),
					new BigDecimal(1)));
			graph.setDx(ObjectUtil.defaultIfNull(form.getDx(), 0));
			graph.setDy(ObjectUtil.defaultIfNull(form.getDy(), 0));
			graph.setGridEnabled(
					ObjectUtil.defaultIfNull(form.getGridEnabled(), true));
			graph.setGridSize(ObjectUtil.defaultIfNull(form.getGridSize(), 1));
			graph.setGridColor(form.getGridColor());
			graph.setPageVisible(
					ObjectUtil.defaultIfNull(form.getPageVisible(), false));
			graph.setBackground(form.getBackground());
			graph.setConnectionArrowsEnabled(ObjectUtil
					.defaultIfNull(form.getConnectionArrowsEnabled(), false));
			graph.setConnectable(
					ObjectUtil.defaultIfNull(form.getConnectable(), true));
			graph.setGuidesEnabled(
					ObjectUtil.defaultIfNull(form.getGuidesEnabled(), true));
			graph.setXml(Base64.decodeStr(form.getXml()));
			graph.setUpdatedAt(new Date());
			iGraphService.updateById(graph);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}