package cn.renlm.graph.controller.sys;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.modular.sys.dto.SysUserDto;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.plugins.MyResponse.Datagrid;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.graph.service.SysAuthAccessService;

/**
 * 角色授权
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping("/sys/authAccess")
public class SysAuthAccessController {

	@Autowired
	private SysAuthAccessService sysAuthAccessService;

	/**
	 * 获取角色授权资源列表
	 * 
	 * @param authAccessed
	 * @param roleId
	 * @param root
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(boolean authAccessed, String roleId, boolean root, Long id) {
		return sysAuthAccessService.getTree(authAccessed, roleId, root, id);
	}

	/**
	 * 添加授权
	 * 
	 * @param roleId
	 * @param resourceIds
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/grant")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> grant(String roleId, String resourceIds) {
		try {
			sysAuthAccessService.grant(roleId, StrUtil.splitTrim(resourceIds, COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 取消授权
	 * 
	 * @param roleId
	 * @param resourceIds
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/unGrant")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> unGrant(String roleId, String resourceIds) {
		try {
			sysAuthAccessService.unGrant(roleId, StrUtil.splitTrim(resourceIds, COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 授权人员分页列表
	 * 
	 * @param page
	 * @param roleId
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getPageUsers")
	public Datagrid<SysUserDto> getPageUsers(Page<SysUser> page, String roleId, SysUserDto form) {
		Page<SysUserDto> pager = sysAuthAccessService.getPageUsers(page, roleId, form);
		return Datagrid.of(pager);
	}

	/**
	 * 添加授权人员
	 * 
	 * @param request
	 * @param roleId
	 * @param userIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/addRoleMember")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> addRoleMember(HttpServletRequest request, String roleId, String userIds) {
		try {
			sysAuthAccessService.addRoleMember(roleId, StrUtil.splitTrim(userIds, COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 移除授权人员
	 * 
	 * @param request
	 * @param roleId
	 * @param userIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/removeRoleMember")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> removeRoleMember(HttpServletRequest request, String roleId, String userIds) {
		try {
			sysAuthAccessService.removeRoleMember(roleId, StrUtil.splitTrim(userIds, COMMA));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}