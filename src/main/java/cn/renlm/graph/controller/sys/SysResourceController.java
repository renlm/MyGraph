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
import cn.renlm.crawler.Result;
import cn.renlm.crawler.consts.ResourceType;
import cn.renlm.crawler.security.DynamicFilterInvocationSecurityMetadataSource;
import cn.renlm.crawler.sys.dto.ResourceDto;
import cn.renlm.crawler.sys.dto.User;
import cn.renlm.crawler.sys.entity.SysResource;
import cn.renlm.crawler.sys.service.ISysResourceService;
import cn.renlm.crawler.utils.SpringSecurityUtil;

/**
 * 资源
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/resource")
public class SysResourceController {

	@Autowired
	private ISysResourceService iSysResourceService;

	/**
	 * 资源管理
	 * 
	 * @return
	 */
	@RequestMapping
	public String resource() {
		return "sys/resource";
	}

	/**
	 * 获取当前用户指定父节点下级菜单
	 * 
	 * @param request
	 * @param pid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listMenuByPid")
	public List<ResourceDto> listMenuByPid(HttpServletRequest request, Long pid) {
		User user = SpringSecurityUtil.getPrincipal(request, User.class);
		return iSysResourceService.findChildsByUserId(user.getUserId(), pid, ResourceType.menu,
				ResourceType.urlNewWindows, ResourceType.urlInsidePage);
	}

	/**
	 * 获取当前用户常用菜单
	 * 
	 * @param request
	 * @param text
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listCommonMenuByText")
	public List<ResourceDto> listCommonMenuByText(HttpServletRequest request, String text) {
		User user = SpringSecurityUtil.getPrincipal(request, User.class);
		return iSysResourceService.findCommonMenusByUserId(user.getUserId(), text);
	}

	/**
	 * 获取指定层级资源列表
	 * 
	 * @param request
	 * @param level
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listByLevel")
	public List<ResourceDto> listByLevel(HttpServletRequest request, Integer level) {
		return iSysResourceService.findListByLevel(level);
	}

	/**
	 * 获取指定父节点下级资源列表
	 * 
	 * @param request
	 * @param pid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/listByPid")
	public List<ResourceDto> listByPid(HttpServletRequest request, Long pid) {
		return iSysResourceService.findListByPid(pid);
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
		List<SysResource> fathers = iSysResourceService.findFathers(id);
		return fathers.stream().map(it -> String.valueOf(it.getId())).collect(Collectors.joining(StrUtil.COMMA));
	}

	/**
	 * 资源弹窗
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model) {
		return "sys/resourceDialog";
	}

	/**
	 * 根据资源ID获取详细信息
	 * 
	 * @param request
	 * @param resourceId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getDetailByResourceId")
	public ResourceDto getDetailByResourceId(HttpServletRequest request, String resourceId) {
		return iSysResourceService.findDetailByResourceId(resourceId);
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
	public Result ajaxSave(HttpServletRequest request, SysResource sysResource) {
		try {
			SysResource exists = iSysResourceService
					.getOne(Wrappers.<SysResource>lambdaQuery().eq(SysResource::getCode, sysResource.getCode()));
			if (StrUtil.isBlank(sysResource.getResourceId())) {
				// 校验资源编码（是否存在）
				if (exists != null) {
					return Result.error("资源编码重复");
				}
				sysResource.setResourceId(IdUtil.getSnowflakeNextIdStr());
				sysResource.setCreatedAt(new Date());
			} else {
				SysResource entity = iSysResourceService.getOne(Wrappers.<SysResource>lambdaQuery()
						.eq(SysResource::getResourceId, sysResource.getResourceId()));
				// 校验资源编码（是否存在）
				if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
					return Result.error("资源编码重复");
				}
				sysResource.setId(entity.getId());
				sysResource.setCreatedAt(entity.getCreatedAt());
				sysResource.setUpdatedAt(new Date());
				sysResource.setDeleted(entity.getDeleted());
			}
			if (sysResource.getPid() == null) {
				sysResource.setLevel(1);
			} else {
				SysResource parent = iSysResourceService.getById(sysResource.getPid());
				sysResource.setLevel(parent.getLevel() + 1);
				List<SysResource> fathers = iSysResourceService.findFathers(parent.getId());
				List<Long> fatherIds = fathers.stream().map(SysResource::getId).collect(Collectors.toList());
				if (fatherIds.contains(sysResource.getId())) {
					return Result.error("不能选择自身或子节点作为父级资源");
				}
			}
			iSysResourceService.saveOrUpdate(sysResource);
			DynamicFilterInvocationSecurityMetadataSource.allConfigAttributes.clear();
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 级联删除
	 * 
	 * @param request
	 * @param resourceId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/del")
	public Result ajaxDel(HttpServletRequest request, String resourceId) {
		try {
			int cnt = iSysResourceService.delCascadeByResourceId(resourceId);
			return Result.success(cnt);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}