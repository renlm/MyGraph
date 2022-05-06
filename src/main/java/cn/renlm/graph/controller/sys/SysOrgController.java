package cn.renlm.graph.controller.sys;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
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
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(Long id) {
		List<Tree<Long>> tree = iSysOrgService.getTree(id);
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
	public Result<SysOrg> ajaxSave(HttpServletRequest request, SysOrg sysOrg) {
		try {
			SysOrg exists = iSysOrgService.getOne(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getCode, sysOrg.getCode()));
			if (StrUtil.isBlank(sysOrg.getOrgId())) {
				// 校验组织机构代码（是否存在）
				if (exists != null) {
					return Result.error("组织机构代码重复");
				}
				sysOrg.setOrgId(IdUtil.getSnowflakeNextIdStr());
				sysOrg.setCreatedAt(new Date());
			} else {
				SysOrg entity = iSysOrgService
						.getOne(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getOrgId, sysOrg.getOrgId()));
				// 校验组织机构代码（是否存在）
				if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
					return Result.error("组织机构代码重复");
				}
				sysOrg.setId(entity.getId());
				sysOrg.setCreatedAt(entity.getCreatedAt());
				sysOrg.setUpdatedAt(new Date());
				sysOrg.setDeleted(entity.getDeleted());
			}
			if (sysOrg.getPid() == null) {
				sysOrg.setLevel(1);
			} else {
				SysOrg parent = iSysOrgService.getById(sysOrg.getPid());
				sysOrg.setLevel(parent.getLevel() + 1);
				List<SysOrg> fathers = iSysOrgService.findFathers(parent.getId());
				List<Long> fatherIds = fathers.stream().map(SysOrg::getId).collect(Collectors.toList());
				if (fatherIds.contains(sysOrg.getId())) {
					return Result.error("不能选择自身或子节点作为父级");
				}
			}
			iSysOrgService.saveOrUpdate(sysOrg);
			return Result.success(sysOrg);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}