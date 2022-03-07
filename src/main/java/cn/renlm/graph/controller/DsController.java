package cn.renlm.graph.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.DsDto;
import cn.renlm.graph.dto.UserDto;
import cn.renlm.graph.entity.Ds;
import cn.renlm.graph.service.IDsService;

/**
 * 数据源
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/ds")
public class DsController {

	@Autowired
	private IDsService iDsService;

	/**
	 * 列表页
	 * 
	 * @return
	 */
	@RequestMapping("/list")
	public String list() {
		return "ds/list";
	}

	/**
	 * 列表数据
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/list")
	public List<Ds> ajaxList(Authentication authentication, DsDto form) {
		UserDto user = (UserDto) authentication.getPrincipal();
		return iDsService.list(Wrappers.<Ds>lambdaQuery().func(wrapper -> {
			wrapper.like(StrUtil.isNotBlank(form.getKeywords()), Ds::getUrl, form.getKeywords());
			wrapper.eq(Ds::getCreatorUserId, user.getUserId());
			wrapper.eq(Ds::getDeleted, false);
			wrapper.orderByDesc(Ds::getUpdatedAt);
			wrapper.orderByDesc(Ds::getId);
		}));
	}

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param authentication
	 * @param ds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	public Result ajaxSave(Authentication authentication, DsDto ds) {
		try {
			UserDto user = (UserDto) authentication.getPrincipal();
			return iDsService.init(user, ds);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 删除
	 * 
	 * @param authentication
	 * @param uuids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/del")
	public Result ajaxDel(Authentication authentication, String uuids) {
		try {
			UserDto user = (UserDto) authentication.getPrincipal();
			List<String> uuidList = StrUtil.splitTrim(uuids, StrUtil.COMMA);
			uuidList.forEach(uuid -> {
				iDsService.delByUuid(user, uuid);
			});
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}