package cn.renlm.graph.controller.ds;

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
import cn.renlm.graph.modular.ds.dto.DsDto;
import cn.renlm.graph.modular.ds.entity.Ds;
import cn.renlm.graph.modular.ds.service.IDsService;
import cn.renlm.plugins.MyResponse.Datagrid;
import cn.renlm.plugins.MyResponse.Result;

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
	 * 分页列表
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Datagrid<Ds> ajaxPage(Authentication authentication, Page<Ds> page, DsDto form) {
		User user = (User) authentication.getPrincipal();
		Page<Ds> data = iDsService.findPage(page, user, form);
		return Datagrid.of(data);
	}

	/**
	 * 弹窗（新建|编辑）
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String uuid) {
		Ds ds = new Ds();
		if (StrUtil.isNotBlank(uuid)) {
			Ds entity = iDsService.getOne(Wrappers.<Ds>lambdaQuery().eq(Ds::getUuid, uuid));
			entity.setPassword(null);
			BeanUtil.copyProperties(entity, ds);
		}
		model.put("ds", ds);
		return "ds/dialog";
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
			uuidList.forEach(uuid -> {
				iDsService.delByUuid(user, uuid);
			});
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}