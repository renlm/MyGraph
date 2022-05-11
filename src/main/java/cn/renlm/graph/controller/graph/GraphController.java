package cn.renlm.graph.controller.graph;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.graph.dto.GraphDto;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.service.IGraphService;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.service.ISysFileService;
import cn.renlm.graph.response.Datagrid;
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
	private ISysFileService iSysFileService;

	/**
	 * 我的
	 * 
	 * @return
	 */
	@GetMapping("/mine")
	public String mine() {
		return "graph/mine";
	}

	/**
	 * 分页列表（我的）
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/mine/ajax/page")
	public Datagrid<Graph> mineAjaxPage(Authentication authentication, Page<Graph> page, GraphDto form) {
		User user = (User) authentication.getPrincipal();
		form.setCreatorUserId(user.getUserId());
		Page<Graph> data = iGraphService.findPage(page, form);
		return Datagrid.of(data);
	}

	/**
	 * 我的弹窗（新建|编辑）
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/mine/dialog")
	public String mineDialog(ModelMap model, String uuid) {
		Graph graph = new Graph();
		if (StrUtil.isNotBlank(uuid)) {
			Graph entity = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
			BeanUtil.copyProperties(entity, graph);
		}
		model.put("graph", graph);
		return "graph/mineDialog";
	}

	/**
	 * 保存我的（新建|编辑）
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/mine/ajax/save")
	public Result<?> mineAjaxSave(Authentication authentication, GraphDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iGraphService.mineAjaxSave(user, form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 删除我的
	 * 
	 * @param authentication
	 * @param uuids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/mine/ajax/del")
	public Result<?> mineAjaxDel(Authentication authentication, String uuids) {
		try {
			if (StrUtil.isBlank(uuids)) {
				return Result.error("请选择要删除的图形");
			}
			List<String> uuidList = StrUtil.splitTrim(uuids, StrUtil.COMMA);
			if (CollUtil.isEmpty(uuidList)) {
				return Result.error("请选择要删除的图形");
			}
			User user = (User) authentication.getPrincipal();
			iGraphService.update(Wrappers.<Graph>lambdaUpdate().func(wrapper -> {
				wrapper.set(Graph::getDeleted, true);
				wrapper.set(Graph::getUpdatedAt, new Date());
				wrapper.set(Graph::getUpdatorUserId, user.getUserId());
				wrapper.set(Graph::getUpdatorNickname, user.getNickname());
				wrapper.eq(Graph::getCreatorUserId, user.getUserId());
				wrapper.in(Graph::getUuid, uuidList);
			}));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 封面上传
	 * 
	 * @param authentication
	 * @param uuid
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/uploadCover")
	public Result<String> uploadCover(Authentication authentication, String uuid, MultipartFile file) {
		try {
			User user = (User) authentication.getPrincipal();
			String originalFilename = file.getOriginalFilename();
			byte[] bytes = file.getBytes();
			SysFile image = iSysFileService.upload(originalFilename, bytes, sysFile -> {
				sysFile.setCreatorUserId(user.getUserId());
				sysFile.setCreatorNickname(user.getNickname());
			});
			iGraphService.update(Wrappers.<Graph>lambdaUpdate().func(wrapper -> {
				wrapper.set(Graph::getCover, image.getFileId());
				wrapper.set(Graph::getUpdatedAt, new Date());
				wrapper.set(Graph::getUpdatorUserId, user.getUserId());
				wrapper.set(Graph::getUpdatorNickname, user.getNickname());
				wrapper.eq(Graph::getCreatorUserId, user.getUserId());
				wrapper.in(Graph::getUuid, uuid);
			}));
			return Result.success(image.getFileId());
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 公共图库
	 * 
	 * @return
	 */
	@GetMapping("/lib")
	public String list() {
		return "graph/lib";
	}

	/**
	 * 分页列表（公共图库）
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
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
	@GetMapping("/editor")
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
	@GetMapping("/viewer")
	public String viewer(ModelMap model, String uuid) {
		Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
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
	public Result<?> saveEditor(Authentication authentication, GraphDto form) {
		User user = (User) authentication.getPrincipal();
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
			return Result.error("出错了");
		}
	}
}