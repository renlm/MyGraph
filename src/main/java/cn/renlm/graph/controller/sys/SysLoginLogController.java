package cn.renlm.graph.controller.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.modular.sys.entity.SysLoginLog;
import cn.renlm.graph.modular.sys.service.ISysLoginLogService;
import cn.renlm.plugins.MyResponse.Datagrid;

/**
 * 系统登录日志
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping("/sys/loginLog")
public class SysLoginLogController {

	@Autowired
	private ISysLoginLogService iSysLoginLogService;

	/**
	 * 日志列表
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping
	public String loginLog(ModelMap model) {
		return "sys/loginLog";
	}

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Datagrid<SysLoginLog> page(Page<SysLoginLog> page, SysLoginLog form) {
		Page<SysLoginLog> data = iSysLoginLogService.findPage(page, form);
		return Datagrid.of(data);
	}
}