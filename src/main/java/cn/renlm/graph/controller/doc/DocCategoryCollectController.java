package cn.renlm.graph.controller.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryCollectDto;
import cn.renlm.graph.modular.doc.service.IDocCategoryCollectService;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;

/**
 * 文档分类-收藏
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/doc/categoryCollect")
public class DocCategoryCollectController {

	@Autowired
	private IDocCategoryCollectService iDocCategoryCollectService;

	/**
	 * 添加收藏/取消收藏
	 * 
	 * @param type            操作类型，0：取消收藏，1：添加收藏
	 * @param authentication  登录用户
	 * @param docProjectUuid  项目Uuid
	 * @param docCategoryUuid 分类Uuid
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/optCollect")
	public Result<?> optCollect(int type, Authentication authentication, String docProjectUuid,
			String docCategoryUuid) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDocCategoryCollectService.optCollect(type, user, docProjectUuid, docCategoryUuid);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
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
	public Datagrid<DocCategoryCollectDto> ajaxPage(Authentication authentication, Page<DocCategoryCollectDto> page,
			DocCategoryCollectDto form) {
		User user = (User) authentication.getPrincipal();
		Page<DocCategoryCollectDto> data = iDocCategoryCollectService.findPage(page, user, form);
		return Datagrid.of(data);
	}
}