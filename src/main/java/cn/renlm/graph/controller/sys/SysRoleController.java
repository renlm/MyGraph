package cn.renlm.graph.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.service.ISysRoleService;
import cn.renlm.graph.response.Result;

/**
 * 角色
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

	@Autowired
	private ISysRoleService iSysRoleService;

	/**
	 * 角色管理
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping
	public String index(ModelMap model) {
		return "sys/role";
	}

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findListByPid")
	public List<SysRole> findListByPid(Long id) {
		return iSysRoleService.findListByPid(id);
	}

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findFathers")
	public List<SysRole> findFathers(Long id) {
		return iSysRoleService.findFathers(id);
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
		List<Tree<Long>> tree = iSysRoleService.getTree(root, id);
		return tree;
	}

	/**
	 * 弹窗
	 * 
	 * @param model
	 * @param pid
	 * @param roleId
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, Long pid, String roleId) {
		SysRole sysRole = new SysRole();
		sysRole.setDisabled(false);
		sysRole.setPid(pid);
		if (StrUtil.isNotBlank(roleId)) {
			SysRole entity = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
			BeanUtil.copyProperties(entity, sysRole);
		}
		model.put("sysRole", sysRole);
		return "sys/roleDialog";
	}

	/**
	 * 保存组织机构（新建|编辑）
	 * 
	 * @param request
	 * @param sysRole
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	@PreAuthorize(Role.AdminSpEL)
	public Result<SysRole> ajaxSave(HttpServletRequest request, SysRole sysRole) {
		try {
			return iSysRoleService.ajaxSave(sysRole);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}