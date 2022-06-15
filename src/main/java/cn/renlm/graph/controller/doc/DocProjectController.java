package cn.renlm.graph.controller.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectDto;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.entity.DocProjectMember;
import cn.renlm.graph.modular.doc.service.IDocProjectMemberService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;

/**
 * 文档项目
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/doc/project")
public class DocProjectController {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocProjectMemberService iDocProjectMemberService;

	/**
	 * 主页
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@GetMapping("/index")
	public String index(ModelMap model, String uuid) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, uuid));
		model.put("docProject", docProject);
		return "doc/projectIndex";
	}

	/**
	 * 分页列表
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Datagrid<DocProjectDto> ajaxPage(Authentication authentication, Page<DocProjectDto> page,
			DocProjectDto form) {
		User user = (User) authentication.getPrincipal();
		Page<DocProjectDto> data = iDocProjectService.findPage(page, user, form);
		return Datagrid.of(data);
	}

	/**
	 * 弹窗（新建|编辑）
	 * 
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String uuid) {
		DocProject docProject = new DocProject();
		docProject.setVisitLevel(1);
		docProject.setIsShare(true);
		docProject.setIsExport(true);
		if (StrUtil.isNotBlank(uuid)) {
			DocProject entity = iDocProjectService
					.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, uuid));
			BeanUtil.copyProperties(entity, docProject);
		}
		model.put("docProject", docProject);
		return "doc/projectDialog";
	}

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/save")
	public Result<DocProjectDto> ajaxSave(Authentication authentication, DocProjectDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDocProjectService.ajaxSave(user, form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 删除
	 * 
	 * @param authentication
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/del")
	public Result<?> ajaxDel(Authentication authentication, String uuid) {
		User user = (User) authentication.getPrincipal();
		DocProject entity = iDocProjectService.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, uuid));
		long members = iDocProjectMemberService.count(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocProjectMember::getRole, 3);
			wrapper.eq(DocProjectMember::getDocProjectId, entity.getId());
			wrapper.eq(DocProjectMember::getMemberUserId, user.getUserId());
			wrapper.eq(DocProjectMember::getDeleted, false);
		}));
		if (members == 0) {
			return Result.of(HttpStatus.FORBIDDEN, "非管理员，无操作权限");
		}
		try {
			return iDocProjectService.delByUuid(user, uuid);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}