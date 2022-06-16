package cn.renlm.graph.controller.doc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.response.Result;

/**
 * 文档分类
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/doc/category")
public class DocCategoryController {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	/**
	 * 主页
	 * 
	 * @param model
	 * @param authentication
	 * @param docProjectUuid
	 * @return
	 */
	@GetMapping
	public String index(ModelMap model, Authentication authentication, String docProjectUuid) {
		User user = (User) authentication.getPrincipal();
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		Integer role = iDocProjectService.findRole(user, docProject.getId());
		model.put("docProject", docProject);
		model.put("role", role);
		return "doc/category";
	}

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param docProjectUuid
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findListByPid")
	public List<DocCategory> findListByPid(String docProjectUuid, Long id) {
		return iDocCategoryService.findListByPid(docProjectUuid, id);
	}

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param docProjectUuid
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/findFathers")
	public List<DocCategory> findFathers(String docProjectUuid, Long id) {
		return iDocCategoryService.findFathers(docProjectUuid, id);
	}

	/**
	 * 获取树形结构
	 * 
	 * @param docProjectUuid
	 * @param root
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getTree")
	public List<Tree<Long>> getTree(String docProjectUuid, boolean root, Long id) {
		List<Tree<Long>> tree = iDocCategoryService.getTree(docProjectUuid, root, id);
		return tree;
	}

	/**
	 * 弹窗（新增|编辑）
	 * 
	 * @param model
	 * @param docProjectUuid
	 * @param pid
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String docProjectUuid, Long pid, String uuid) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		DocCategory docCategory = new DocCategory();
		docCategory.setDocProjectId(docProject.getId());
		docCategory.setPid(pid);
		if (StrUtil.isNotBlank(uuid)) {
			DocCategory entity = iDocCategoryService.getOne(Wrappers.<DocCategory>lambdaQuery().func(wrapper -> {
				wrapper.eq(DocCategory::getDocProjectId, docProject.getId());
				wrapper.eq(DocCategory::getUuid, uuid);
			}));
			BeanUtil.copyProperties(entity, docCategory);
		}
		model.put("docProject", docProject);
		model.put("docCategory", docCategory);
		return "doc/categoryDialog";
	}

	/**
	 * 保存|更新
	 * 
	 * @param authentication
	 * @param docProjectUuid
	 * @param docCategory
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	public Result<DocCategory> ajaxSave(Authentication authentication, String docProjectUuid, DocCategory docCategory) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDocCategoryService.ajaxSave(user, docProjectUuid, docCategory);
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
		try {
			return iDocCategoryService.delByUuid(user, uuid);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}