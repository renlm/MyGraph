package cn.renlm.graph.controller.sys;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleService;
import cn.renlm.graph.security.UserService;
import cn.renlm.plugins.MyResponse.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 资源
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping("/sys/resource")
public class SysResourceController {

	@Autowired
	private UserService userService;

	@Autowired
	private ISysRoleService iSysRoleService;

	@Autowired
	private ISysResourceService iSysResourceService;

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	/**
	 * 获取菜单列表
	 * 
	 * @param request
	 * @param response
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getMenus")
	public List<Tree<Long>> getMenus(HttpServletRequest request, HttpServletResponse response, String uuid) {
		User user = userService.refreshUserDetails(request, response);
		List<Tree<Long>> tree = user.getMenuTree(uuid);
		return tree;
	}

	/**
	 * 资源管理
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping
	public String index(ModelMap model) {
		return "sys/resource";
	}

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findListByPid")
	public List<SysResource> findListByPid(Long id) {
		return iSysResourceService.findListByPid(id);
	}

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findFathers")
	public List<SysResource> findFathers(Long id) {
		return iSysResourceService.findFathers(id);
	}

	/**
	 * 获取树形结构
	 * 
	 * @param root
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(boolean root, Long id) {
		List<Tree<Long>> tree = iSysResourceService.getTree(root, id, true);
		return tree;
	}

	/**
	 * 资源弹窗
	 * 
	 * @param model
	 * @param pid
	 * @param resourceId
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, Long pid, String resourceId) {
		SysResource sysResource = new SysResource();
		sysResource.setDefaultHomePage(false);
		sysResource.setDisabled(false);
		sysResource.setPid(pid);
		if (StrUtil.isNotBlank(resourceId)) {
			SysResource entity = iSysResourceService
					.getOne(Wrappers.<SysResource>lambdaQuery().eq(SysResource::getResourceId, resourceId));
			BeanUtil.copyProperties(entity, sysResource);
		}
		model.put("sysResource", sysResource);
		return "sys/resourceDialog";
	}

	/**
	 * 保存资源（新建|编辑）
	 * 
	 * @param request
	 * @param sysResource
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	@PreAuthorize(Role.AdminSpEL)
	public Result<SysResource> ajaxSave(HttpServletRequest request, SysResource sysResource) {
		try {
			return iSysResourceService.ajaxSave(sysResource);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 更新角色自定义参数
	 * 
	 * @param request
	 * @param roleId
	 * @param resourceId
	 * @param alias
	 * @param sort
	 * @param defaultHomePage
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/updateRoleCustom")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> updateRoleCustom(HttpServletRequest request, String roleId, String resourceId, String alias,
			Integer sort, Boolean defaultHomePage) {
		try {
			SysRole sysRole = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
			SysResource sysResource = iSysResourceService
					.getOne(Wrappers.<SysResource>lambdaQuery().eq(SysResource::getResourceId, resourceId));
			iSysRoleResourceService.update(Wrappers.<SysRoleResource>lambdaUpdate().func(wrapper -> {
				wrapper.set(SysRoleResource::getAlias, alias);
				wrapper.set(SysRoleResource::getSort, sort);
				wrapper.set(SysRoleResource::getDefaultHomePage, defaultHomePage);
				wrapper.set(SysRoleResource::getUpdatedAt, new Date());
				wrapper.eq(SysRoleResource::getSysRoleId, sysRole.getId());
				wrapper.eq(SysRoleResource::getSysResourceId, sysResource.getId());
			}));
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

}
