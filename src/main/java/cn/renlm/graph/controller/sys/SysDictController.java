package cn.renlm.graph.controller.sys;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
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
	 * 字典列表
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping
	public String index(ModelMap model) {
		String actuator = rsa.encryptBase64("cn.renlm.crawler.sys.service.ISysDictService.exportDataToFile",
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
	 * 获取树形字典
	 * 
	 * @param codePaths
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(String codePaths) {
		List<Tree<Long>> tree = iSysDictService.getTree(StrUtil.splitToArray(codePaths, StrUtil.COMMA));
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
	 * @param request
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/imp")
	public Result<List<String>> imp(Authentication authentication, String fileId) {
		try {
			User user = (User) authentication.getPrincipal();
			return iSysDictService.importDataFromFile(user, fileId);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
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
	public SysDict getDetailByUuid(HttpServletRequest request, String uuid) {
		return iSysDictService.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getUuid, uuid));
	}
}