package cn.renlm.graph.controller.sys;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.service.ISysDictService;
import cn.renlm.graph.response.Result;

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
	private RSA rsa;

	@Autowired
	private ISysDictService iSysDictService;

	/**
	 * 通用字典
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping
	public String index(ModelMap model) {
		String actuator = rsa.encryptBase64("cn.renlm.graph.modular.sys.service.ISysDictService.exportDataToFile",
				KeyType.PrivateKey);
		model.put("actuator", Base64.encodeUrlSafe(actuator));
		return "sys/dict";
	}

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findListByPid")
	public List<SysDict> findListByPid(Long id) {
		return iSysDictService.findListByPid(id);
	}

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findFathers")
	public List<SysDict> findFathers(Long id) {
		return iSysDictService.findFathers(id);
	}

	/**
	 * 获取树形字典
	 * 
	 * @param root
	 * @param codePaths
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(boolean root, String codePaths) {
		List<Tree<Long>> tree = iSysDictService.getTree(root, StrUtil.splitToArray(codePaths, StrUtil.COMMA));
		return tree;
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
	 * @param authentication
	 * @param file
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/imp")
	@PreAuthorize(Role.AdminSpEL)
	public Result<List<String>> imp(Authentication authentication, MultipartFile file) {
		try {
			User user = (User) authentication.getPrincipal();
			return iSysDictService.importDataFromFile(user, file);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 字典弹窗
	 * 
	 * @param model
	 * @param pid
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, Long pid, String uuid) {
		SysDict sysDict = new SysDict();
		sysDict.setDisabled(false);
		sysDict.setPid(pid);
		if (StrUtil.isNotBlank(uuid)) {
			SysDict entity = iSysDictService.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getUuid, uuid));
			BeanUtil.copyProperties(entity, sysDict);
		}
		model.put("sysDict", sysDict);
		return "sys/dictDialog";
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
	@PreAuthorize(Role.AdminSpEL)
	public Result<SysDict> ajaxSave(HttpServletRequest request, SysDict sysDict) {
		try {
			return iSysDictService.ajaxSave(sysDict);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}