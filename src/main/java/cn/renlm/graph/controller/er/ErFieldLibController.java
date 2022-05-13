package cn.renlm.graph.controller.er;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldLibDto;
import cn.renlm.graph.modular.er.entity.ErFieldLib;
import cn.renlm.graph.modular.er.service.IErFieldLibService;
import cn.renlm.graph.response.Datagrid;

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
	@GetMapping("/ajax/list")
	public Datagrid<ErFieldLib> list(Authentication authentication, Page<ErFieldLib> page, ErFieldLibDto form) {
		User user = (User) authentication.getPrincipal();
		Page<ErFieldLib> data = iErFieldLibService.findPage(page, user, form);
		return Datagrid.of(data);
	}
}