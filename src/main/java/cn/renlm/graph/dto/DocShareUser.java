package cn.renlm.graph.dto;

import java.io.Serializable;

import org.springframework.ui.ModelMap;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.extra.spring.SpringUtil;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.service.PubDocService;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.plugins.MyResponse.StatusCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 认证用户（文档分享）
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
public class DocShareUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String key = DocShareUser.class.getName();

	/**
	 * 分享Uuid
	 */
	private String shareUuid;

	/**
	 * 验证密码
	 * 
	 * @param request
	 * @param model
	 * @param shareUuid
	 * @param password
	 * @return
	 */
	public static final Result<?> verifyPassword(HttpServletRequest request, ModelMap model, String shareUuid,
			String password) {
		PubDocService pubDocService = SpringUtil.getBean(PubDocService.class);
		RSA rsa = SpringUtil.getBean(RSA.class);
		DocCategoryShareDto docCategoryShare = pubDocService.getDocCategoryShare(shareUuid);
		String decryptStr = rsa.decryptStr(docCategoryShare.getPassword(), KeyType.PublicKey);
		model.put("shareUuid", shareUuid);
		model.put("docCategoryShare", docCategoryShare);
		if (StrUtil.isBlank(shareUuid)) {
			return Result.of(StatusCode.BAD_REQUEST, "参数缺失");
		}
		if (StrUtil.isBlank(password)) {
			return Result.of(StatusCode.BAD_REQUEST, "请输入密码");
		}
		if (StrUtil.equals(decryptStr, password)) {
			DocShareUser docShareUser = new DocShareUser();
			docShareUser.setShareUuid(shareUuid);
			request.getSession().setAttribute(key + shareUuid, docShareUser);
			return Result.success();
		} else {
			return Result.of(StatusCode.BAD_REQUEST, "密码错误");
		}
	}

	/**
	 * 获取认证用户信息
	 * 
	 * @param request
	 * @param shareUuid
	 * @return
	 */
	public static final DocShareUser getInfo(HttpServletRequest request, String shareUuid) {
		return (DocShareUser) request.getSession().getAttribute(key + shareUuid);
	}

}
