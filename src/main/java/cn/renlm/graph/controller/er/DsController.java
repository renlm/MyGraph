package cn.renlm.graph.controller.er;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.DsDto;
import cn.renlm.graph.modular.er.entity.Ds;
import cn.renlm.graph.modular.er.service.IDsService;
import cn.renlm.graph.response.Result;

/**
 * 数据源
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/ds")
public class DsController {

	@Autowired
	private IDsService iDsService;

	/**
	 * 列表页
	 * 
	 * @return
	 */
	@GetMapping("/list")
	public String list() {
		return "ds/list";
	}

	/**
	 * 列表数据
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/list")
	public Page<Ds> ajaxList(Authentication authentication, Page<Ds> page, DsDto form) {
		User user = (User) authentication.getPrincipal();
		return iDsService.findPage(page, user, form);
	}

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param authentication
	 * @param ds
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/save")
	public Result<String> ajaxSave(Authentication authentication, DsDto ds) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDsService.init(user, ds);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 删除
	 * 
	 * @param authentication
	 * @param uuids
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/del")
	public Result<?> ajaxDel(Authentication authentication, String uuids) {
		try {
			User user = (User) authentication.getPrincipal();
			List<String> uuidList = StrUtil.splitTrim(uuids, StrUtil.COMMA);
			uuidList.forEach(uuid -> {
				iDsService.delByUuid(user, uuid);
			});
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}