package cn.renlm.graph.controller.graph;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.amqp.AmqpUtil;
import cn.renlm.graph.amqp.GraphCoverQueue;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.graph.dto.GraphDto;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.entity.GraphHistory;
import cn.renlm.graph.modular.graph.service.IGraphHistoryService;
import cn.renlm.graph.modular.graph.service.IGraphService;
import cn.renlm.graph.response.Result;

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

	@Resource
	private IGraphHistoryService iGraphHistoryService;

	/**
	 * 我的图库
	 * 
	 * @return
	 */
	@GetMapping("/lib")
	public String list() {
		return "graph/lib";
	}

	/**
	 * 分页列表（我的图库）
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Page<GraphDto> publicPage(Authentication authentication, Page<GraphDto> page, GraphDto form) {
		User user = (User) authentication.getPrincipal();
		return iGraphService.findPage(page, user, form);
	}

	/**
	 * 选择器（数据库表）
	 * 
	 * @return
	 */
	@RequestMapping("/dbTableSelector")
	public String dbTableSelector() {
		return "graph/dbTableSelector";
	}

	/**
	 * 选择器（我的字段库）
	 * 
	 * @return
	 */
	@RequestMapping("/dbFieldLibSelector")
	public String dbFieldLibSelector() {
		return "graph/dbFieldLibSelector";
	}

	/**
	 * 图形编辑
	 * 
	 * @param model
	 * @param uuid
	 * @param name
	 * @return
	 */
	@GetMapping("/editor")
	public String editor(ModelMap model, String uuid, String name) {
		Graph graph = new Graph();
		graph.setUuid(uuid);
		graph.setName(name);
		graph.setVersion(1);
		GraphDto.fillDefault(graph);
		if (StrUtil.isNotBlank(uuid)) {
			Graph entity = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
			if (ObjectUtil.isNotEmpty(entity)) {
				BeanUtil.copyProperties(entity, graph);
			}
		}
		graph.setXml(StrUtil.isBlank(graph.getXml()) ? null : Base64.encodeUrlSafe(graph.getXml()));
		model.put("graphJson", JSONUtil.toJsonStr(graph));
		return "graph/editor";
	}

	/**
	 * 图形预览
	 * 
	 * @param model
	 * @param uuid
	 * @param version
	 * @param name
	 * @return
	 */
	@GetMapping("/viewer")
	public String viewer(ModelMap model, String uuid, Integer version, String name) {
		Graph graph = new Graph();
		graph.setUuid(uuid);
		graph.setName(name);
		graph.setVersion(version);
		if (StrUtil.isNotBlank(uuid)) {
			if (version == null) {
				Graph entity = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
				if (ObjectUtil.isNotEmpty(entity)) {
					BeanUtil.copyProperties(entity, graph);
				}
			} else {
				GraphHistory history = iGraphHistoryService
						.getOne(Wrappers.<GraphHistory>lambdaQuery().func(wrapper -> {
							wrapper.eq(GraphHistory::getGraphUuid, uuid);
							wrapper.eq(GraphHistory::getVersion, version);
						}));
				if (ObjectUtil.isNotEmpty(history)) {
					BeanUtil.copyProperties(history, graph);
					graph.setId(history.getGraphId());
					graph.setUuid(history.getGraphUuid());
				}
			}
		}
		graph.setXml(StrUtil.isBlank(graph.getXml()) ? null : Base64.encodeUrlSafe(graph.getXml()));
		model.put("graphJson", JSONUtil.toJsonStr(graph));
		return "graph/viewer";
	}

	/**
	 * 保存编辑器
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/saveEditor")
	public Result<Graph> saveEditor(Authentication authentication, GraphDto form) {
		User user = (User) authentication.getPrincipal();
		try {
			Result<Graph> result = iGraphService.saveEditor(user, form);
			AmqpUtil.createQueue(GraphCoverQueue.EXCHANGE, GraphCoverQueue.ROUTINGKEY, form.getUuid());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}