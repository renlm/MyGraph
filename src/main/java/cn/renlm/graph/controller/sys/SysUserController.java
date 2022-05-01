package cn.renlm.graph.controller.sys;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController {

	/**
	 * 弹窗（个人信息）
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/personalDialog")
	public String personalDialog(ModelMap model) {
		return "modifyPersonal";
	}
}
