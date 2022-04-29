package cn.renlm.graph.controller.sys;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.crawler.Result;
import cn.renlm.crawler.security.UserService;
import cn.renlm.crawler.sys.dto.User;
import cn.renlm.crawler.sys.dto.UserDto;
import cn.renlm.crawler.sys.entity.SysUser;
import cn.renlm.crawler.sys.service.ISysUserOrgService;
import cn.renlm.crawler.sys.service.ISysUserRoleService;
import cn.renlm.crawler.sys.service.ISysUserService;
import cn.renlm.crawler.utils.SpringSecurityUtil;

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

	@Autowired
	private ISysUserOrgService iSysUserOrgService;

	@Autowired
	private ISysUserRoleService iSysUserRoleService;

	/**
	 * 用户信息
	 * 
	 * @return
	 */
	@RequestMapping
	public String org() {
		return "sys/user";
	}

	/**
	 * 用户列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/page")
	public Page<SysUser> page(Page<SysUser> page, UserDto form) {
		return iSysUserService.findPage(page, form);
	}

	/**
	 * 弹窗（新增|编辑）
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model) {
		return "sys/userDialog";
	}

	/**
	 * 弹窗（个人信息）
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/selfInfoDialog")
	public String selfInfoDialog(ModelMap model) {
		return "sys/userInfoDialog";
	}

	/**
	 * 弹窗（修改密码）
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/selfPasswdDialog")
	public String selfPasswdDialog(ModelMap model) {
		return "sys/userPasswdDialog";
	}

	/**
	 * 根据当前登录用户信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getSelfDetail")
	public UserDto getSelfDetail(HttpServletRequest request) {
		User user = SpringSecurityUtil.getPrincipal(request, User.class);
		return iSysUserService.findByUserId(user.getUserId());
	}

	/**
	 * 根据用户ID获取详细信息
	 * 
	 * @param request
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getDetailByUserId")
	public UserDto getDetailByUserId(HttpServletRequest request, String userId) {
		return iSysUserService.findByUserId(userId);
	}

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param request
	 * @param sysUser
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	public Result ajaxSave(HttpServletRequest request, UserDto sysUser) {
		try {
			// 校验账号（格式）
			if (!ReUtil.isMatch(SpringSecurityUtil.username_reg, sysUser.getUsername())) {
				return Result.error(SpringSecurityUtil.username_msg);
			}
			SysUser exists = iSysUserService
					.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, sysUser.getUsername()));
			if (sysUser.getUserId() == null) {
				// 校验账号（是否存在）
				if (exists != null) {
					return Result.error("登录账号已存在");
				}
				// 校验密码（格式）
				if (!ReUtil.isMatch(SpringSecurityUtil.password_reg, sysUser.getPassword())) {
					return Result.error(SpringSecurityUtil.password_msg);
				}
				sysUser.setUserId(IdUtil.simpleUUID().toUpperCase());
				sysUser.setPassword(new BCryptPasswordEncoder().encode(sysUser.getPassword()));
				sysUser.setCreatedAt(new Date());
			} else {
				SysUser entity = iSysUserService
						.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserId, sysUser.getUserId()));
				// 校验账号（是否存在）
				if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
					return Result.error("登录账号已存在");
				}
				sysUser.setId(entity.getId());
				sysUser.setPassword(entity.getPassword());
				sysUser.setAccountNonExpired(entity.getAccountNonExpired());
				sysUser.setCredentialsNonExpired(entity.getCredentialsNonExpired());
				sysUser.setCreatedAt(entity.getCreatedAt());
				sysUser.setUpdatedAt(new Date());
			}
			iSysUserService.saveOrUpdate(sysUser);
			iSysUserOrgService.saveRelationships(sysUser.getUserId(),
					StrUtil.splitTrim(sysUser.getOrgIds(), StrUtil.COMMA));
			iSysUserRoleService.saveRelationships(sysUser.getUserId(),
					StrUtil.splitTrim(sysUser.getRoleIds(), StrUtil.COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 修改个人信息
	 * 
	 * @param request
	 * @param sysUser
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/saveSelfInfo")
	public Result saveSelfInfo(HttpServletRequest request, UserDto sysUser) {
		User user = SpringSecurityUtil.getPrincipal(request, User.class);
		try {
			// 校验账号（格式）
			if (!ReUtil.isMatch(SpringSecurityUtil.username_reg, sysUser.getUsername())) {
				return Result.error(SpringSecurityUtil.username_msg);
			}
			SysUser exists = iSysUserService
					.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, sysUser.getUsername()));
			SysUser entity = iSysUserService.getById(user.getId());
			// 校验账号（是否存在）
			if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
				return Result.error("登录账号已存在");
			}
			sysUser.setId(entity.getId());
			sysUser.setUserId(entity.getUserId());
			sysUser.setPassword(entity.getPassword());
			sysUser.setEnabled(entity.getEnabled());
			sysUser.setAccountNonLocked(entity.getAccountNonLocked());
			sysUser.setAccountNonExpired(entity.getAccountNonExpired());
			sysUser.setCredentialsNonExpired(entity.getCredentialsNonExpired());
			sysUser.setCreatedAt(entity.getCreatedAt());
			sysUser.setUpdatedAt(new Date());
			iSysUserService.saveOrUpdate(sysUser);
			userService.refreshAuthentication(request, sysUser.getUsername());
			return Result.success(sysUser.setPassword(null));
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 修改密码
	 * 
	 * @param request
	 * @param sysUser
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/modifyPwd")
	public Result modifyPwd(HttpServletRequest request, UserDto sysUser) {
		User user = SpringSecurityUtil.getPrincipal(request, User.class);
		try {
			if (!StrUtil.equals(sysUser.getNewPassword(), sysUser.getConfirmPassword())) {
				return Result.error("两次输入的密码不一致");
			}
			// 校验密码（格式）
			if (!ReUtil.isMatch(SpringSecurityUtil.password_reg, sysUser.getNewPassword())) {
				return Result.error(SpringSecurityUtil.password_msg);
			}
			SysUser entity = iSysUserService.getById(user.getId());
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			if (!passwordEncoder.matches(sysUser.getPassword(), entity.getPassword())) {
				return Result.error("旧密码错误");
			}
			entity.setPassword(passwordEncoder.encode(sysUser.getNewPassword()));
			entity.setUpdatedAt(new Date());
			iSysUserService.saveOrUpdate(entity);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 批量重置用户密码
	 * 
	 * @param request
	 * @param userIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/resetPassword")
	public Result resetPassword(HttpServletRequest request, String userIds) {
		try {
			if (StrUtil.isBlank(userIds)) {
				return Result.error("未选中用户");
			}
			String newPassword = RandomUtil.randomString(RandomUtil.randomInt(8, 12));
			List<SysUser> users = iSysUserService.list(
					Wrappers.<SysUser>lambdaQuery().in(SysUser::getUserId, StrUtil.splitTrim(userIds, StrUtil.COMMA)));
			users.forEach(user -> {
				user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
			});
			iSysUserService.saveOrUpdateBatch(users);
			return Result.success().setMessage(newPassword);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}