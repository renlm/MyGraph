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
import cn.renlm.graph.modular.sys.dto.OrgChartDto;
import cn.renlm.graph.modular.sys.dto.OrgDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.service.ISysOrgService;

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
	 * 组织机构管理
	 * 
	 * @return
	 */
	@RequestMapping
	public String org() {
		return "sys/org";
	}

	/**
	 * 组织机构树
	 * 
	 * @return
	 */
	@RequestMapping("/chart")
	public String chart() {
		return "sys/orgChart";
	}

	/**
	 * 获取组织机构树
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listTree")
	public List<OrgChartDto> listTree(HttpServletRequest request) {
		return iSysOrgService.findTreeList();
	}

	/**
	 * 获取指定层级组织机构列表
	 * 
	 * @param request
	 * @param level
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listByLevel")
	public List<OrgDto> listByLevel(HttpServletRequest request, Integer level) {
		return iSysOrgService.findListByLevel(level);
	}

	/**
	 * 获取指定父节点下级组织机构列表
	 * 
	 * @param request
	 * @param pid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listByPid")
	public List<OrgDto> listByPid(HttpServletRequest request, Long pid) {
		return iSysOrgService.findListByPid(pid);
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
		List<SysOrg> fathers = iSysOrgService.findFathers(id);
		return fathers.stream().map(it -> String.valueOf(it.getId())).collect(Collectors.joining(StrUtil.COMMA));
	}

	/**
	 * 组织机构弹窗
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model) {
		return "sys/orgDialog";
	}

	/**
	 * 根据组织机构ID获取详细信息
	 * 
	 * @param request
	 * @param orgId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getDetailByOrgId")
	public OrgDto getDetailByOrgId(HttpServletRequest request, String orgId) {
		return iSysOrgService.findDetailByOrgId(orgId);
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
	public Result<?> ajaxSave(HttpServletRequest request, SysOrg sysOrg) {
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
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param orgId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/del")
	public Result<Integer> ajaxDel(HttpServletRequest request, String orgId) {
		try {
			int cnt = iSysOrgService.delCascadeByOrgId(orgId);
			return Result.success(cnt);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}