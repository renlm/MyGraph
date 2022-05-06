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
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.security.DynamicFilterInvocationSecurityMetadataSource;
import cn.renlm.graph.security.UserService;

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
	private UserService userService;

	@Autowired
	private ISysResourceService iSysResourceService;

	/**
	 * 获取菜单列表
	 * 
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getMenus")
	public List<Tree<Long>> getMenus(String uuid) {
		User user = userService.refreshAuthentication();
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
	public Result<SysResource> ajaxSave(HttpServletRequest request, SysResource sysResource) {
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
			return Result.success(sysResource);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}