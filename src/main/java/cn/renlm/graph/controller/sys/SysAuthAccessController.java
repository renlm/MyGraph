package cn.renlm.graph.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.modular.sys.dto.ResourceDto;
import cn.renlm.graph.modular.sys.dto.UserDto;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.service.SysAuthAccessService;

/**
 * 角色授权
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/authAccess")
public class SysAuthAccessController {

	@Autowired
	private SysAuthAccessService sysAuthAccessService;

	/**
	 * 获取全部资源列表并指定角色授权状态
	 * 
	 * @param request
	 * @param roleId  角色ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAllResourceByRoleId")
	public List<ResourceDto> getAllResourceByRoleId(HttpServletRequest request, String roleId) {
		return sysAuthAccessService.getAllResourceByRoleId(roleId);
	}

	/**
	 * 获取角色授权资源列表
	 * 
	 * @param request
	 * @param roleId  角色ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAuthAccessResourceByRoleId")
	public List<ResourceDto> getAuthAccessResourceByRoleId(HttpServletRequest request, String roleId) {
		return sysAuthAccessService.getAuthAccessResourceByRoleId(roleId);
	}

	/**
	 * 角色添加授权
	 * 
	 * @param request
	 * @param roleId      角色ID
	 * @param resourceIds 资源ID集（多个逗号分隔）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/grant")
	public Result<List<ResourceDto>> grant(HttpServletRequest request, String roleId, String resourceIds) {
		try {
			List<ResourceDto> list = sysAuthAccessService.grant(roleId, StrUtil.splitTrim(resourceIds, StrUtil.COMMA));
			return Result.success(list);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 角色取消授权
	 * 
	 * @param request
	 * @param roleId      角色ID
	 * @param resourceIds 资源ID集（多个逗号分隔）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ungrant")
	public Result<List<ResourceDto>> ungrant(HttpServletRequest request, String roleId, String resourceIds) {
		try {
			List<ResourceDto> list = sysAuthAccessService.ungrant(roleId,
					StrUtil.splitTrim(resourceIds, StrUtil.COMMA));
			return Result.success(list);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 分页获取全部用户列表并指定角色授权状态
	 * 
	 * @param page
	 * @param roleId 角色ID
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAllUserByRoleIdAndPage")
	public Page<UserDto> getAllUserByRoleIdAndPage(Page<SysUser> page, String roleId, UserDto form) {
		return sysAuthAccessService.getAllUserByRoleIdAndPage(page, roleId, form);
	}

	/**
	 * 添加角色授权人员
	 * 
	 * @param request
	 * @param roleId  角色ID
	 * @param userIds 用户ID集（多个逗号分隔）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addRoleMember")
	public Result<?> addRoleMember(HttpServletRequest request, String roleId, String userIds) {
		try {
			roleId = CollUtil.getFirst(CollUtil.distinct(StrUtil.splitTrim(roleId, StrUtil.COMMA)));
			sysAuthAccessService.addRoleMember(roleId, StrUtil.splitTrim(userIds, StrUtil.COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 移除角色授权人员
	 * 
	 * @param request
	 * @param roleId  角色ID
	 * @param userIds 用户ID集（多个逗号分隔）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/removeRoleMember")
	public Result<?> removeRoleMember(HttpServletRequest request, String roleId, String userIds) {
		try {
			roleId = CollUtil.getFirst(CollUtil.distinct(StrUtil.splitTrim(roleId, StrUtil.COMMA)));
			sysAuthAccessService.removeRoleMember(roleId, StrUtil.splitTrim(userIds, StrUtil.COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}