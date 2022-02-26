package cn.renlm.graph.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

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
	 * 编辑器
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/editor")
	public String editor(ModelMap model, String uuid) {
		Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
		model.put("graphJson", JSONUtil.toJsonStr(graph));
		return "graph/editor";
	}

	/**
	 * 保存编辑器
	 * 
	 * @param request
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/saveEditor")
	public Result saveEditor(HttpServletRequest request, GraphDto form) {
		try {
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}