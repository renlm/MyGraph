package cn.renlm.graph.controller.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.service.IDocCategoryCollectService;
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
}