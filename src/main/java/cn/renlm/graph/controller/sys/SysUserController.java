package cn.renlm.graph.controller.sys;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.service.ISysUserService;
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
			SysUser entity = iSysUserService.getById(user.getId());
			entity.setNickname(form.getNickname());
			entity.setRealname(form.getRealname());
			entity.setIdCard(form.getIdCard());
			entity.setSex(form.getSex());
			entity.setBirthday(form.getBirthday());
			entity.setMobile(form.getMobile());
			entity.setEmail(form.getEmail());
			entity.setRemark(form.getRemark());
			entity.setUpdatedAt(new Date());
			iSysUserService.updateById(entity);
			User refreshUser = userService.refreshAuthentication();
			return Result.success(refreshUser);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}