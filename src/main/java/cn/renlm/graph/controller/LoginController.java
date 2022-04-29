package cn.renlm.graph.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.ConstVal;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.modular.sys.dto.User;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.service.ISysUserService;

/**
 * 登录
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping
public class LoginController {

	@Autowired
	private ISysUserService iSysUserService;

	/**
	 * 登录页
	 * 
	 * @return
	 */
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	/**
	 * 注册页
	 * 
	 * @return
	 */
	@RequestMapping("/register")
	public String register() {
		return "register";
	}

	/**
	 * 注册账号
	 * 
	 * @param form
	 * @param confirmpwd
	 * @return
	 */
	@ResponseBody
	@PostMapping("/doRegister")
	public Result<?> doRegister(User form, String confirmpwd) {
		try {
			SysUser user = iSysUserService.getOne(Wrappers.<SysUser>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysUser::getUsername, form.getUsername());
			}));
			if (user != null) {
				return Result.error("账号已存在");
			}
			if (!StrUtil.equals(form.getPassword(), confirmpwd)) {
				return Result.error("两次输入的密码不一致");
			}
			if (!ReUtil.isMatch(ConstVal.password_reg, form.getPassword())) {
				return Result.error(ConstVal.password_msg);
			}
			form.setUserId(IdUtil.simpleUUID().toUpperCase());
			form.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
			form.setNickname(ObjectUtil.defaultIfBlank(form.getNickname(), form.getUsername()));
			form.setCreatedAt(new Date());
			form.setEnabled(true);
			iSysUserService.save(form);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("系统异常");
		}
	}

	/**
	 * 密码修改页
	 * 
	 * @return
	 */
	@RequestMapping("/modifyPwd")
	public String modifyPwd() {
		return "modifyPwd";
	}

	/**
	 * 修改密码
	 * 
	 * @param authentication
	 * @param password
	 * @param confirmpwd
	 * @return
	 */
	@ResponseBody
	@PostMapping("/doModifyPwd")
	public Result<?> doModifyPwd(Authentication authentication, String password, String confirmpwd) {
		User user = (User) authentication.getPrincipal();
		try {
			if (!StrUtil.equals(password, confirmpwd)) {
				return Result.error("两次输入的密码不一致");
			}
			if (!ReUtil.isMatch(ConstVal.password_reg, password)) {
				return Result.error(ConstVal.password_msg);
			}
			iSysUserService.update(Wrappers.<SysUser>lambdaUpdate().func(wrapper -> {
				wrapper.set(SysUser::getPassword, new BCryptPasswordEncoder().encode(password));
				wrapper.set(SysUser::getUpdatedAt, new Date());
				wrapper.eq(SysUser::getId, user.getId());
			}));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("系统异常");
		}
	}
}