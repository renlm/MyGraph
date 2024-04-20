package cn.renlm.mygraph.controller.doc;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.mygraph.modular.doc.entity.DocCategory;
import cn.renlm.mygraph.modular.doc.entity.DocCategoryShare;
import cn.renlm.mygraph.modular.doc.service.IDocCategoryService;
import cn.renlm.mygraph.modular.doc.service.IDocCategoryShareService;
import cn.renlm.mygraph.properties.MyConfigProperties;
import cn.renlm.plugins.MyResponse.Datagrid;
import cn.renlm.plugins.MyResponse.Result;
import jakarta.annotation.Resource;

/**
 * 文档分类-分享
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping("/doc/categoryShare")
public class DocCategoryShareController {

	@Resource
	private RSA rsa;

	@Autowired
	private MyConfigProperties myConfigProperties;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Autowired
	private IDocCategoryShareService iDocCategoryShareService;

	/**
	 * 关闭分享
	 * 
	 * @param authentication
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/closeShare")
	public Result<?> closeShare(Authentication authentication, String uuid) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDocCategoryShareService.closeShare(user, uuid);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 分享地址展示
	 * 
	 * @param authentication
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/show")
	public String show(Authentication authentication, ModelMap model, String uuid) {
		User user = (User) authentication.getPrincipal();
		DocCategoryShare docCategoryShare = iDocCategoryShareService
				.getOne(Wrappers.<DocCategoryShare>lambdaQuery().func(wrapper -> {
					wrapper.eq(DocCategoryShare::getUuid, uuid);
					wrapper.eq(DocCategoryShare::getCreatorUserId, user.getUserId());
				}));
		if (ObjectUtil.isNotEmpty(docCategoryShare)) {
			model.put("ctx", myConfigProperties.getCtx());
			model.put("docCategoryShare", docCategoryShare);
			if (docCategoryShare.getShareType() == 2) {
				String password = rsa.decryptStr(docCategoryShare.getPassword(), KeyType.PublicKey);
				model.put("password", password);
			}
		}
		return "doc/categoryShareShow";
	}

	/**
	 * 弹窗（新建）
	 * 
	 * @param model
	 * @param docProjectUuid
	 * @param docCategoryUuid
	 * @return
	 */
	@RequestMapping("/dialog")
	public String dialog(ModelMap model, String docProjectUuid, String docCategoryUuid) {
		DocCategory docCategory = iDocCategoryService
				.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, docCategoryUuid));
		List<DocCategory> fathers = iDocCategoryService.findFathers(docProjectUuid, docCategory.getId());
		DocCategoryShare docCategoryShare = new DocCategoryShare();
		docCategoryShare.setShareType(1);
		docCategoryShare.setEffectiveType(-1);
		model.put("docProjectUuid", docProjectUuid);
		model.put("docCategoryUuid", docCategoryUuid);
		model.put("docCategoryName",
				fathers.stream().map(DocCategory::getText).collect(Collectors.joining(StrUtil.SLASH)));
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
	public Result<?> ajaxSave(Authentication authentication, DocCategoryShareDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			return iDocCategoryShareService.ajaxSave(user, form);
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
	public Datagrid<DocCategoryShareDto> ajaxPage(Authentication authentication, Page<DocCategoryShareDto> page,
			DocCategoryShareDto form) {
		User user = (User) authentication.getPrincipal();
		Page<DocCategoryShareDto> data = iDocCategoryShareService.findPage(page, user, form);
		return Datagrid.of(data);
	}
}