package cn.renlm.graph.controller.sys;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.crawler.Result;
import cn.renlm.crawler.sys.dto.RoleResourceDto;
import cn.renlm.crawler.sys.entity.SysRoleResource;
import cn.renlm.crawler.sys.service.ISysRoleResourceService;

/**
 * 角色资源关系
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/roleResource")
public class SysRoleResourceController {

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	/**
	 * 角色资源关系弹窗
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model) {
		return "sys/roleResourceDialog";
	}

	/**
	 * 根据主键ID获取详细信息
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/getDetailById")
	public RoleResourceDto getDetailById(HttpServletRequest request, Long id) {
		return iSysRoleResourceService.findDetailById(id);
	}

	/**
	 * 更新
	 * 
	 * @param request
	 * @param sysRoleResource
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/update")
	public Result ajaxUpdate(HttpServletRequest request, SysRoleResource sysRoleResource) {
		try {
			SysRoleResource entity = iSysRoleResourceService.getById(sysRoleResource.getId());
			entity.setAlias(sysRoleResource.getAlias());
			entity.setSort(sysRoleResource.getSort());
			entity.setCommonly(sysRoleResource.getCommonly());
			entity.setDefaultHomePage(sysRoleResource.getDefaultHomePage());
			entity.setHide(sysRoleResource.getHide());
			entity.setUpdatedAt(new Date());
			iSysRoleResourceService.updateById(entity);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}