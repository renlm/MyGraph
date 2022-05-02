package cn.renlm.graph.controller.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.dto.SysUserDto;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.service.ISysUserService;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.security.UserService;

/**
 * 用户
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ISysUserService iSysUserService;

	/**
	 * 用户列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/page")
	public Datagrid<SysUser> page(Page<SysUser> page, SysUserDto form) {
		Page<SysUser> data = iSysUserService.findPage(page, form);
		return Datagrid.of(data);
	}

	/**
	 * 弹窗（个人信息）
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/personalDialog")
	public String personalDialog(ModelMap model) {
		return "modifyPersonal";
	}

	/**
	 * 修改个人信息
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/doModifyPersonal")
	public Result<?> doModifyPersonal(Authentication authentication, SysUser form) {
		User user = (User) authentication.getPrincipal();
		try {
			iSysUserService.doModifyPersonal(user.getId(), form);
			User refreshUser = userService.refreshAuthentication();
			return Result.success(refreshUser);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}