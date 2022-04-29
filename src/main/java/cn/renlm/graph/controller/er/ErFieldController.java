package cn.renlm.graph.controller.er;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.entity.ErField;
import cn.renlm.graph.modular.er.service.IErFieldService;

/**
 * ER模型-字段
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/erField")
public class ErFieldController {

	@Autowired
	private IErFieldService iErFieldService;

	/**
	 * 获取ER模型字段列表
	 * 
	 * @param request
	 * @param erUuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/list")
	public List<ErField> list(HttpServletRequest request, String erUuid) {
		return iErFieldService.findListByErUuid(erUuid);
	}

	/**
	 * 更新
	 * 
	 * @param authentication
	 * @param uuid
	 * @param name
	 * @param comment
	 * @param type
	 * @param size
	 * @param digit
	 * @param isNullable
	 * @param autoIncrement
	 * @param columnDef
	 * @param isPk
	 * @param isFk
	 * @param remark
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/update")
	public Result<?> ajaxUpdate(Authentication authentication, String uuid, String name, String comment, Integer type,
			Integer size, Integer digit, Boolean isNullable, Boolean autoIncrement, String columnDef, Boolean isPk,
			Boolean isFk, String remark) {
		try {
			User user = (User) authentication.getPrincipal();
			ErField entity = iErFieldService.getOne(Wrappers.<ErField>lambdaQuery().eq(ErField::getUuid, uuid));
			entity.setName(name);
			entity.setComment(comment);
			entity.setType(type);
			entity.setSize(size);
			entity.setDigit(digit);
			entity.setIsNullable(isNullable);
			entity.setAutoIncrement(autoIncrement);
			entity.setColumnDef(columnDef);
			entity.setIsPk(isPk);
			entity.setIsFk(isFk);
			entity.setRemark(remark);
			entity.setUpdatedAt(new Date());
			entity.setUpdatorUserId(user.getUserId());
			entity.setUpdatorNickname(user.getNickname());
			iErFieldService.updateById(entity);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}