package cn.renlm.mygraph.controller;

import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.AES;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.common.ConstVal;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.sys.entity.SysUser;
import cn.renlm.mygraph.modular.sys.service.ISysUserService;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.mygraph.util.SessionUtil;

/**
 * 登录
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping
public class LoginController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ISysUserService iSysUserService;

	/**
	 * 登录页
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/login")
	public String login(HttpServletRequest request, ModelMap model) {
		String aesKey = SessionUtil.getAesKey(request);
		model.put(SessionUtil.AESKey, aesKey);
		return "login";
	}

	/**
	 * 密码修改页
	 * 
	 * @return
	 */
	@GetMapping("/modifyPwd")
	public String modifyPwd() {
		return "modifyPwd";
	}

	/**
	 * 修改密码
	 * 
	 * @param request
	 * @param authentication
	 * @param _password
	 * @param password
	 * @param confirmpwd
	 * @return
	 */
	@ResponseBody
	@PostMapping("/doModifyPwd")
	public Result<?> doModifyPwd(HttpServletRequest request, Authentication authentication, String _password,
			String password, String confirmpwd) {
		String aesKey = SessionUtil.getAesKey(request);
		User user = (User) authentication.getPrincipal();
		User userDetails = iSysUserService.loadUserByUsername(user.getUsername());
		try {
			_password = AES.decrypt(_password, aesKey);
			password = AES.decrypt(password, aesKey);
			confirmpwd = AES.decrypt(confirmpwd, aesKey);
			if (!passwordEncoder.matches(_password, userDetails.getPassword())) {
				return Result.error("密码错误");
			}
			if (!StrUtil.equals(password, confirmpwd)) {
				return Result.error("两次输入的密码不一致");
			}
			if (!ReUtil.isMatch(ConstVal.password_reg, password)) {
				return Result.error(ConstVal.password_msg);
			}
			String pwdEncode = passwordEncoder.encode(password);
			iSysUserService.update(Wrappers.<SysUser>lambdaUpdate().func(wrapper -> {
				wrapper.set(SysUser::getPassword, pwdEncode);
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