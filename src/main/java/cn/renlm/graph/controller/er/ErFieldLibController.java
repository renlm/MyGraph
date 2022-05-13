package cn.renlm.graph.controller.er;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldLibDto;
import cn.renlm.graph.modular.er.entity.ErFieldLib;
import cn.renlm.graph.modular.er.service.IErFieldLibService;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;

/**
 * ER模型-我的字段库
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/erFieldLib")
public class ErFieldLibController {

	@Autowired
	private IErFieldLibService iErFieldLibService;

	/**
	 * 分页列表
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Datagrid<ErFieldLib> page(Authentication authentication, Page<ErFieldLib> page, ErFieldLibDto form) {
		User user = (User) authentication.getPrincipal();
		Page<ErFieldLib> data = iErFieldLibService.findPage(page, user, form);
		return Datagrid.of(data);
	}

	/**
	 * 弹窗（新增|编辑）
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String uuid) {
		ErFieldLib erFieldLib = new ErFieldLib();
		if (StrUtil.isNotBlank(uuid)) {
			ErFieldLib entity = iErFieldLibService
					.getOne(Wrappers.<ErFieldLib>lambdaQuery().eq(ErFieldLib::getUuid, uuid));
			BeanUtil.copyProperties(entity, erFieldLib);
		}
		model.put("erFieldLib", erFieldLib);
		return "er/fieldLibDialog";
	}

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	public Result<?> ajaxSave(Authentication authentication, ErFieldLibDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iErFieldLibService.ajaxSave(user, form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}