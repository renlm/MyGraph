package cn.renlm.graph.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.ErDto;
import cn.renlm.graph.dto.UserDto;
import cn.renlm.graph.entity.Er;
import cn.renlm.graph.mxgraph.ERModelParser;
import cn.renlm.graph.service.IErService;

/**
 * ER模型
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/er")
public class ErController {

	@Autowired
	private IErService iErService;

	@Autowired
	private ERModelParser eRModelParser;

	/**
	 * 分页列表
	 * 
	 * @param request
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/page")
	public Page<ErDto> page(HttpServletRequest request, Page<Er> page, ErDto form) {
		return iErService.findPage(page, form);
	}

	/**
	 * 更新
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/update")
	public Result ajaxUpdate(Authentication authentication, ErDto form) {
		try {
			UserDto user = (UserDto) authentication.getPrincipal();
			Er entity = iErService.getOne(Wrappers.<Er>lambdaQuery().eq(Er::getUuid, form.getUuid()));
			entity.setUpdatedAt(new Date());
			entity.setUpdatorUserId(user.getUserId());
			entity.setUpdatorNickname(user.getNickname());
			iErService.updateById(entity);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}

	/**
	 * 创建ER模型图
	 * 
	 * @param request
	 * @param name
	 * @param uuids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/createGraph")
	public Result createGraph(Authentication authentication, String name, String uuids) {
		try {
			UserDto user = (UserDto) authentication.getPrincipal();
			List<String> uuidList = StrUtil.splitTrim(uuids, StrUtil.COMMA);
			List<ErDto> ers = iErService.findListWithFields(uuidList);
			eRModelParser.create(user, name, ers);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}