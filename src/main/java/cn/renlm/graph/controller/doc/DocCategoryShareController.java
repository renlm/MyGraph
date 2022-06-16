package cn.renlm.graph.controller.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategoryShare;
import cn.renlm.graph.modular.doc.service.IDocCategoryShareService;
import cn.renlm.graph.response.Result;

/**
 * 文档分类-分享
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/doc/categoryShare")
public class DocCategoryShareController {

	@Autowired
	private IDocCategoryShareService iDocCategoryShareService;

	/**
	 * 弹窗（新建）
	 * 
	 * @param model
	 * @param docCategoryUuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String docCategoryUuid) {
		DocCategoryShare docCategoryShare = new DocCategoryShare();
		docCategoryShare.setShareType(1);
		docCategoryShare.setEffectiveType(-1);
		model.put("docCategoryUuid", docCategoryUuid);
		model.put("docCategoryShare", docCategoryShare);
		return "doc/categoryShareDialog";
	}

	/**
	 * 保存（新建）
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/save")
	public Result<DocCategoryShareDto> ajaxSave(Authentication authentication, DocCategoryShareDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDocCategoryShareService.ajaxSave(user, form);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}