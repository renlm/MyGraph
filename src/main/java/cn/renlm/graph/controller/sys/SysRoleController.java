package cn.renlm.graph.controller.sys;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.modular.sys.dto.RoleDto;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.service.ISysRoleService;

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
	 * @return
	 */
	@RequestMapping
	public String role() {
		return "sys/role";
	}

	/**
	 * 获取角色树
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listTree")
	public List<SysRole> listTree(HttpServletRequest request) {
		return iSysRoleService.findTreeList();
	}

	/**
	 * 获取指定层级角色列表
	 * 
	 * @param request
	 * @param level
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listByLevel")
	public List<RoleDto> listByLevel(HttpServletRequest request, Integer level) {
		return iSysRoleService.findListByLevel(level);
	}

	/**
	 * 获取指定父节点下级角色列表
	 * 
	 * @param request
	 * @param pid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listByPid")
	public List<RoleDto> listByPid(HttpServletRequest request, Long pid) {
		return iSysRoleService.findListByPid(pid);
	}

	/**
	 * 获取由下而上的子父id集
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getFatherIds")
	public String getFatherIds(HttpServletRequest request, Long id) {
		List<SysRole> fathers = iSysRoleService.findFathers(id);
		return fathers.stream().map(it -> String.valueOf(it.getId())).collect(Collectors.joining(StrUtil.COMMA));
	}

	/**
	 * 角色弹窗
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model) {
		return "sys/roleDialog";
	}

	/**
	 * 根据角色ID获取详细信息
	 * 
	 * @param request
	 * @param roleId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getDetailByRoleId")
	public RoleDto getDetailByRoleId(HttpServletRequest request, String roleId) {
		return iSysRoleService.findDetailByRoleId(roleId);
	}

	/**
	 * 保存角色（新建|编辑）
	 * 
	 * @param request
	 * @param sysRole
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	public Result<String> ajaxSave(HttpServletRequest request, SysRole sysRole) {
		try {
			SysRole exists = iSysRoleService
					.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getCode, sysRole.getCode()));
			if (StrUtil.isBlank(sysRole.getRoleId())) {
				// 校验角色代码（是否存在）
				if (exists != null) {
					return Result.error("角色代码重复");
				}
				sysRole.setRoleId(IdUtil.getSnowflakeNextIdStr());
				sysRole.setCreatedAt(new Date());
			} else {
				SysRole entity = iSysRoleService
						.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, sysRole.getRoleId()));
				// 校验角色代码（是否存在）
				if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
					return Result.error("角色代码重复");
				}
				sysRole.setId(entity.getId());
				sysRole.setCreatedAt(entity.getCreatedAt());
				sysRole.setUpdatedAt(new Date());
				sysRole.setDeleted(entity.getDeleted());
			}
			if (sysRole.getPid() == null) {
				sysRole.setLevel(1);
			} else {
				SysRole parent = iSysRoleService.getById(sysRole.getPid());
				sysRole.setLevel(parent.getLevel() + 1);
				List<SysRole> fathers = iSysRoleService.findFathers(parent.getId());
				List<Long> fatherIds = fathers.stream().map(SysRole::getId).collect(Collectors.toList());
				if (fatherIds.contains(sysRole.getId())) {
					return Result.error("不能选择自身或子节点作为父级角色");
				}
			}
			iSysRoleService.saveOrUpdate(sysRole);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}