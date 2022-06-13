package cn.renlm.graph.controller.doc;

import static cn.hutool.core.text.StrPool.COMMA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectMemberDto;
import cn.renlm.graph.modular.doc.service.IDocProjectMemberService;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;

/**
 * 文档项目-成员
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/doc/projectMember")
public class DocProjectMemberController {

	@Autowired
	private IDocProjectMemberService iDocProjectMemberService;

	/**
	 * 成员管理
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String uuid) {
		model.put("uuid", uuid);
		return "doc/projectMemberDialog";
	}

	/**
	 * 分页列表（项目授权人员）
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/authAccessPage")
	public Datagrid<DocProjectMemberDto> ajaxAuthAccessPage(Authentication authentication, Page<SysUser> page,
			DocProjectMemberDto form) {
		User user = (User) authentication.getPrincipal();
		Page<DocProjectMemberDto> data = iDocProjectMemberService.findAuthAccessPage(page, user, form);
		return Datagrid.of(data);
	}

	/**
	 * 添加项目授权人员
	 * 
	 * @param authentication
	 * @param role
	 * @param docProjectUuid
	 * @param userIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/addRoleMember")
	public Result<?> addRoleMember(Authentication authentication, Integer role, String docProjectUuid, String userIds) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDocProjectMemberService.addRoleMember(user, role, docProjectUuid,
					StrUtil.splitTrim(userIds, COMMA));
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 移除项目授权人员
	 * 
	 * @param authentication
	 * @param docProjectUuid
	 * @param userIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/removeRoleMember")
	public Result<?> removeRoleMember(Authentication authentication, String docProjectUuid, String userIds) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDocProjectMemberService.removeRoleMember(user, userIds, StrUtil.splitTrim(userIds, COMMA));
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}