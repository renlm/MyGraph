package cn.renlm.graph.controller.sys;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.dto.SysOrgDto;
import cn.renlm.graph.modular.sys.dto.SysUserDto;
import cn.renlm.graph.modular.sys.entity.SysRole;
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
	 * 用户管理
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping
	public String index(ModelMap model) {
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
	@GetMapping("/ajax/page")
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

	/**
	 * 批量重置用户密码
	 * 
	 * @param request
	 * @param userIds
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/resetPassword")
	public Result<String> resetPassword(HttpServletRequest request, String userIds) {
		try {
			if (StrUtil.isBlank(userIds)) {
				return Result.error("未选中用户");
			}
			String newPassword = RandomUtil.randomString(RandomUtil.randomInt(8, 12));
			List<SysUser> users = iSysUserService
					.list(Wrappers.<SysUser>lambdaQuery().in(SysUser::getUserId, StrUtil.splitTrim(userIds, COMMA)));
			users.forEach(user -> {
				user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
				user.setUpdatedAt(new Date());
			});
			iSysUserService.saveOrUpdateBatch(users);
			return Result.success(newPassword);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 弹窗（新建|编辑）
	 * 
	 * @param model
	 * @param userId
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String userId) {
		SysUserDto userDetail = new SysUserDto();
		userDetail.setEnabled(true);
		if (StrUtil.isNotBlank(userId)) {
			SysUser entity = iSysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserId, userId));
			BeanUtil.copyProperties(entity, userDetail);
			User user = iSysUserService.loadUserByUsername(entity.getUsername());
			userDetail.setPassword(null);
			userDetail.setOrgIds(user.getOrgs().stream().map(SysOrgDto::getOrgId).collect(Collectors.joining(COMMA)));
			userDetail.setRoleIds(user.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.joining(COMMA)));
		}
		model.put("userDetail", userDetail);
		return "sys/userDialog";
	}

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param request
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	public Result<SysUserDto> ajaxSave(HttpServletRequest request, SysUserDto form) {
		try {
			return iSysUserService.ajaxSave(form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}