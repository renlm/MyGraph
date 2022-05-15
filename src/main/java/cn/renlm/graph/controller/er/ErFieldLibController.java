package cn.renlm.graph.controller.er;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	@GetMapping("/dialog")
	public String dialog(ModelMap model, String uuid) {
		ErFieldLib erFieldLib = new ErFieldLib();
		erFieldLib.setAutoIncrement(false);
		erFieldLib.setIsNullable(true);
		if (StrUtil.isNotBlank(uuid)) {
			ErFieldLib entity = iErFieldLibService
					.getOne(Wrappers.<ErFieldLib>lambdaQuery().eq(ErFieldLib::getUuid, uuid));
			BeanUtil.copyProperties(entity, erFieldLib);
		}
		model.put("erFieldLib", erFieldLib);
		return "er/fieldLibDialog";
	}

	/**
	 * 添加字段到我的字段库
	 * 
	 * @param authentication
	 * @param fieldUuid
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/addFieldToLib")
	public Result<ErFieldLib> addFieldToLib(Authentication authentication, String fieldUuid) {
		try {
			User user = (User) authentication.getPrincipal();
			return iErFieldLibService.addFieldToLib(user, fieldUuid);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/save")
	public Result<?> ajaxSave(Authentication authentication, ErFieldLibDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iErFieldLibService.ajaxSave(user, form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
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
			iErFieldLibService.update(Wrappers.<ErFieldLib>lambdaUpdate().func(wrapper -> {
				wrapper.set(ErFieldLib::getDeleted, true);
				wrapper.set(ErFieldLib::getUpdatedAt, new Date());
				wrapper.set(ErFieldLib::getUpdatorUserId, user.getUserId());
				wrapper.set(ErFieldLib::getUpdatorNickname, user.getNickname());
				wrapper.eq(ErFieldLib::getCreatorUserId, user.getUserId());
				wrapper.eq(ErFieldLib::getDeleted, false);
				wrapper.in(ErFieldLib::getUuid, uuidList);
			}));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}