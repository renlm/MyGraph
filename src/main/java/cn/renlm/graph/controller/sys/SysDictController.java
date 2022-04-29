package cn.renlm.graph.controller.sys;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.modular.sys.dto.DictDto;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.service.ISysDictService;
import cn.renlm.graph.security.User;

/**
 * 数据字典
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/dict")
public class SysDictController {

	@Resource
	private RSA crawlRSA;

	@Autowired
	private ISysDictService iSysDictService;

	/**
	 * 字典管理
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping
	public String dict(ModelMap model) {
		String actuator = crawlRSA.encryptBase64("cn.renlm.crawler.sys.service.ISysDictService.exportDataToFile",
				KeyType.PrivateKey);
		model.put("actuator", Base64.encodeUrlSafe(actuator));
		return "sys/dict";
	}

	/**
	 * 弹窗（字典上传）
	 * 
	 * @return
	 */
	@RequestMapping("/uploadDialog")
	public String uploadDialog() {
		return "sys/dictUploadDialog";
	}

	/**
	 * 字典导入
	 * 
	 * @param request
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/imp")
	public Result<List<String>> imp(Authentication authentication, String fileId) {
		try {
			User user = (User) authentication.getPrincipal();
			return iSysDictService.importConfigFromFile(user, fileId);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 根据字典集代码路径获取字典项列表（单级下拉）
	 * 
	 * @param request
	 * @param paths   由上至下编码串
	 * @param empty   空选项名称（不传即无）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getItemsByPaths")
	public List<SysDict> getItemsByPaths(HttpServletRequest request, String paths, String empty) {
		List<SysDict> list = CollUtil.newArrayList();
		if (StrUtil.isNotBlank(empty)) {
			list.add(new SysDict().setText(empty));
		}
		List<SysDict> parents = iSysDictService.findPaths(StrUtil.splitToArray(paths, StrUtil.COMMA));
		if (CollUtil.isEmpty(parents)) {
			return list;
		}
		List<SysDict> items = iSysDictService
				.list(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getPid, CollUtil.getLast(parents).getId()));
		CollUtil.addAll(list, items);
		return list;
	}

	/**
	 * 根据字典集代码路径获取字典项列表（树形结构）
	 * 
	 * @param request
	 * @param paths   由上至下编码串
	 * @param self    是否包含本身（默认不包含）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getTreesByPaths")
	public List<SysDict> getTreesByPaths(HttpServletRequest request, String paths, Boolean self) {
		List<SysDict> list = CollUtil.newArrayList();
		List<SysDict> parents = iSysDictService.findPaths(StrUtil.splitToArray(paths, StrUtil.COMMA));
		if (CollUtil.isEmpty(parents)) {
			return list;
		}
		SysDict node = CollUtil.getLast(parents);
		List<DictDto> items = iSysDictService.findChilds(node.getId());
		CollUtil.addAll(list, items);
		if (!BooleanUtil.isTrue(self)) {
			CollUtil.removeWithAddIf(list, it -> {
				return NumberUtil.equals(it.getId(), node.getId());
			});
		}
		return list;
	}

	/**
	 * 获取字典树
	 * 
	 * @param request
	 * @param paths   由上至下编码串
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listTree")
	public List<SysDict> listTree(HttpServletRequest request, String paths) {
		return iSysDictService.findTreeList(StrUtil.splitToArray(paths, StrUtil.COMMA));
	}

	/**
	 * 获取指定层级字典列表
	 * 
	 * @param request
	 * @param level
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listByLevel")
	public List<DictDto> listByLevel(HttpServletRequest request, Integer level) {
		return iSysDictService.findListByLevel(level);
	}

	/**
	 * 获取指定父节点下级字典列表
	 * 
	 * @param request
	 * @param pid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listByPid")
	public List<DictDto> listByPid(HttpServletRequest request, Long pid) {
		return iSysDictService.findListByPid(pid);
	}

	/**
	 * 获取由下而上的子父id集
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getFatherIds")
	public String getFatherIds(HttpServletRequest request, Long id) {
		List<SysDict> fathers = iSysDictService.findFathers(id);
		return fathers.stream().map(it -> String.valueOf(it.getId())).collect(Collectors.joining(StrUtil.COMMA));
	}

	/**
	 * 字典弹窗
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model) {
		return "sys/dictDialog";
	}

	/**
	 * 根据Uuid获取详细信息
	 * 
	 * @param request
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getDetailByUuid")
	public DictDto getDetailByUuid(HttpServletRequest request, String uuid) {
		return iSysDictService.getDetailByUuid(uuid);
	}

	/**
	 * 保存字典（新建|编辑）
	 * 
	 * @param request
	 * @param sysDict
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	public Result<String> ajaxSave(HttpServletRequest request, SysDict sysDict) {
		try {
			if (StrUtil.isBlank(sysDict.getUuid())) {
				sysDict.setUuid(IdUtil.simpleUUID().toUpperCase());
				sysDict.setCreatedAt(new Date());
			} else {
				SysDict Dict = iSysDictService
						.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getUuid, sysDict.getUuid()));
				sysDict.setId(Dict.getId());
				sysDict.setCreatedAt(Dict.getCreatedAt());
				sysDict.setUpdatedAt(new Date());
			}
			if (sysDict.getPid() == null) {
				sysDict.setLevel(1);
			} else {
				SysDict parent = iSysDictService.getById(sysDict.getPid());
				sysDict.setLevel(parent.getLevel() + 1);
				List<SysDict> fathers = iSysDictService.findFathers(parent.getId());
				List<Long> fatherIds = fathers.stream().map(SysDict::getId).collect(Collectors.toList());
				if (fatherIds.contains(sysDict.getId())) {
					return Result.error("不能选择自身或子节点作为父级字典");
				}
			}
			// 校验字典编码（同级编码不能重复）
			long cnt = iSysDictService.count(Wrappers.<SysDict>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysDict::getCode, sysDict.getCode());
				wrapper.eq(SysDict::getLevel, sysDict.getLevel());
				if (sysDict.getId() != null) {
					wrapper.notIn(SysDict::getId, sysDict.getId());
				}
				if (sysDict.getPid() == null) {
					wrapper.isNull(SysDict::getPid);
				} else {
					wrapper.eq(SysDict::getPid, sysDict.getPid());
				}
			}));
			if (cnt > 0) {
				return Result.error("同级目录字典编码不能重复");
			}
			iSysDictService.saveOrUpdate(sysDict);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}