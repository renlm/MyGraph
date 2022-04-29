package cn.renlm.graph.controller.sys;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.crawler.Result;
import cn.renlm.crawler.sys.dto.UserOrgDto;
import cn.renlm.crawler.sys.entity.SysUserOrg;
import cn.renlm.crawler.sys.service.ISysUserOrgService;

/**
 * 用户组织机构关系
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/userOrg")
public class SysUserOrgController {

	@Autowired
	private ISysUserOrgService iSysUserOrgService;

	/**
	 * 列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/list")
	public Page<UserOrgDto> list(Page<SysUserOrg> page, UserOrgDto form) {
		return iSysUserOrgService.findPage(page, form);
	}

	/**
	 * 更新
	 * 
	 * @param request
	 * @param id
	 * @param positionCode
	 * @param positionName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/update")
	public Result ajaxUpdate(HttpServletRequest request, Long id, String positionCode, String positionName) {
		try {
			SysUserOrg entity = iSysUserOrgService.getById(id);
			entity.setPositionCode(positionCode);
			entity.setPositionName(positionName);
			entity.setUpdatedAt(new Date());
			iSysUserOrgService.updateById(entity);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}