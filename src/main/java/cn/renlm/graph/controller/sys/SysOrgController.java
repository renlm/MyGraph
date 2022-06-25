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
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.response.Result;

/**
 * 组织机构
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/org")
public class SysOrgController {

	@Autowired
	private ISysOrgService iSysOrgService;

	/**
	 * 主页
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping
	public String index(ModelMap model) {
		return "sys/org";
	}

	/**
	 * 组织机构树
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/chart")
	public String chart(ModelMap model, Long id) {
		model.put("id", id);
		return "sys/orgChart";
	}

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findListByPid")
	public List<SysOrg> findListByPid(Long id) {
		return iSysOrgService.findListByPid(id);
	}

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findFathers")
	public List<SysOrg> findFathers(Long id) {
		return iSysOrgService.findFathers(id);
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
		List<Tree<Long>> tree = iSysOrgService.getTree(root, id);
		return tree;
	}

	/**
	 * 弹窗
	 * 
	 * @param model
	 * @param pid
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, Long pid, String orgId) {
		SysOrg sysOrg = new SysOrg();
		sysOrg.setDisabled(false);
		sysOrg.setPid(pid);
		if (StrUtil.isNotBlank(orgId)) {
			SysOrg entity = iSysOrgService.getOne(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getOrgId, orgId));
			BeanUtil.copyProperties(entity, sysOrg);
		}
		model.put("sysOrg", sysOrg);
		return "sys/orgDialog";
	}

	/**
	 * 保存组织机构（新建|编辑）
	 * 
	 * @param request
	 * @param sysOrg
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	@PreAuthorize(Role.AdminSpEL)
	public Result<SysOrg> ajaxSave(HttpServletRequest request, SysOrg sysOrg) {
		try {
			return iSysOrgService.ajaxSave(sysOrg);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}