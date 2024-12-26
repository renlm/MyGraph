package cn.renlm.mygraph.controller.graph;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.mygraph.amqp.AmqpUtil;
import cn.renlm.mygraph.amqp.GraphCoverQueue;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.graph.dto.GraphDto;
import cn.renlm.mygraph.modular.graph.entity.Graph;
import cn.renlm.mygraph.modular.graph.entity.GraphHistory;
import cn.renlm.mygraph.modular.graph.service.IGraphHistoryService;
import cn.renlm.mygraph.modular.graph.service.IGraphService;
import cn.renlm.mygraph.modular.sys.entity.SysFile;
import cn.renlm.mygraph.mxgraph.ERModelParser;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.mygraph.util.RedisUtil;

/**
 * 图形设计
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping("/graph")
public class GraphController {

	@Autowired
	private ERModelParser eRModelParser;

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
	 * 我的弹窗（新建|编辑）
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String mineDialog(ModelMap model, String uuid, String categoryCode) {
		Graph graph = new Graph();
		graph.setUuid(uuid);
		graph.setCategoryCode(categoryCode);
		if (StrUtil.isNotBlank(uuid)) {
			Graph entity = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
			if (ObjectUtil.isNotEmpty(entity)) {
				BeanUtil.copyProperties(entity, graph);
			}
		}
		model.put("graph", graph);
		return "graph/dialog";
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
	 * 更新基本信息
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/updateBaseInfo")
	public Result<?> updateBaseInfo(Authentication authentication, GraphDto form) {
		User user = (User) authentication.getPrincipal();
		try {
			Result<?> result = iGraphService.updateBaseInfo(user, form);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
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
			String key = GraphCoverQueue.QUEUE + IdUtil.simpleUUID().toUpperCase();
			RedisTemplate<String, String> edisTemplate = RedisUtil.getRedisTemplate();
			edisTemplate.opsForValue().set(key, form.getUuid(), 7, TimeUnit.DAYS);
			AmqpUtil.createQueue(GraphCoverQueue.EXCHANGE, GraphCoverQueue.ROUTINGKEY, key);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * ER模型DDL下载
	 * 
	 * @param request
	 * @param response
	 * @param authentication
	 * @param uuid
	 * @throws IOException
	 */
	@GetMapping("/downloadERDDL")
	public void downloadERDDL(HttpServletRequest request, HttpServletResponse response, Authentication authentication, String uuid)
			throws IOException {
		User user = (User) authentication.getPrincipal();
		SysFile file = eRModelParser.generateDDL(user, uuid);
		String filename = URLEncoder.encode(file.getOriginalFilename(), "UTF-8");
		response.setHeader("Content-Type", file.getFileType());
		response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
		try (ServletOutputStream os = response.getOutputStream()) {
			os.write(file.getFileContent());
		}
	}
}