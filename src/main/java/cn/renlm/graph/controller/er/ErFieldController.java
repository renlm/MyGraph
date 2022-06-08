package cn.renlm.graph.controller.er;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.db.meta.JdbcType;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldDto;
import cn.renlm.graph.modular.er.entity.ErField;
import cn.renlm.graph.modular.er.service.IErFieldService;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;

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
	 * @param authentication
	 * @param erUuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/list")
	public Datagrid<ErFieldDto> list(Authentication authentication, String erUuid) {
		User user = (User) authentication.getPrincipal();
		List<ErFieldDto> datas = iErFieldService.findListByEr(user, erUuid);
		return Datagrid.of(datas);
	}

	/**
	 * 更新
	 * 
	 * @param authentication
	 * @param uuid
	 * @param name
	 * @param comment
	 * @param sqlType
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
	public Result<?> ajaxUpdate(Authentication authentication, String uuid, String name, String comment,
			Integer sqlType, Long size, Integer digit, Boolean isNullable, Boolean autoIncrement, String columnDef,
			Boolean isPk, Boolean isFk, String remark) {
		try {
			User user = (User) authentication.getPrincipal();
			ErField entity = iErFieldService.getOne(Wrappers.<ErField>lambdaQuery().eq(ErField::getUuid, uuid));
			entity.setName(name);
			entity.setComment(comment);
			entity.setSqlType(sqlType);
			entity.setJdbcType(JdbcType.valueOf(sqlType).name());
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
			return Result.error("出错了");
		}
	}
}