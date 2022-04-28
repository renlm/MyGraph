package cn.renlm.graph.controller;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.common.Datagrid;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.GraphDto;
import cn.renlm.graph.dto.UserDto;
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
	 * 我的作品
	 * 
	 * @return
	 */
	@RequestMapping("/mine")
	public String mine() {
		return "graph/mine";
	}

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
	 * 我的作品（分页）
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/minePage")
	public Datagrid<Graph> minePage(Authentication authentication, Page<Graph> page, GraphDto form) {
		UserDto user = (UserDto) authentication.getPrincipal();
		form.setCreatorUserId(user.getUserId());
		Page<Graph> data = iGraphService.findPage(page, form);
		return Datagrid.of(data);
	}

	/**
	 * 公共图库（分页）
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/page")
	public Page<Graph> publicPage(Page<Graph> page, GraphDto form) {
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
		Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
		graph.setXml(StrUtil.isBlank(graph.getXml()) ? null : Base64.encodeUrlSafe(graph.getXml()));
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
		Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
		graph.setXml(StrUtil.isBlank(graph.getXml()) ? null : Base64.encodeUrlSafe(graph.getXml()));
		model.put("graphJson", JSONUtil.toJsonStr(graph));
		return "graph/viewer";
	}

	/**
	 * 保存
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/saveEditor")
	public Result<?> saveEditor(Authentication authentication, GraphDto form) {
		UserDto user = (UserDto) authentication.getPrincipal();
		try {
			Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, form.getUuid()));
			graph.setZoom(ObjectUtil.defaultIfNull(form.getZoom(), new BigDecimal(1)));
			graph.setDx(ObjectUtil.defaultIfNull(form.getDx(), 0));
			graph.setDy(ObjectUtil.defaultIfNull(form.getDy(), 0));
			graph.setGridEnabled(ObjectUtil.defaultIfNull(form.getGridEnabled(), true));
			graph.setGridSize(ObjectUtil.defaultIfNull(form.getGridSize(), 1));
			graph.setGridColor(form.getGridColor());
			graph.setPageVisible(ObjectUtil.defaultIfNull(form.getPageVisible(), false));
			graph.setBackground(form.getBackground());
			graph.setConnectionArrowsEnabled(ObjectUtil.defaultIfNull(form.getConnectionArrowsEnabled(), false));
			graph.setConnectable(ObjectUtil.defaultIfNull(form.getConnectable(), true));
			graph.setGuidesEnabled(ObjectUtil.defaultIfNull(form.getGuidesEnabled(), true));
			graph.setXml(Base64.decodeStr(form.getXml()));
			graph.setUpdatedAt(new Date());
			graph.setUpdatorUserId(user.getUserId());
			graph.setUpdatorNickname(user.getNickname());
			iGraphService.updateById(graph);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}