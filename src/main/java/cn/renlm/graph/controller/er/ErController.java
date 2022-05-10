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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErDto;
import cn.renlm.graph.modular.er.entity.Er;
import cn.renlm.graph.modular.er.service.IErService;
import cn.renlm.graph.mxgraph.ERModelParser;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;

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
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Datagrid<ErDto> page(Authentication authentication, Page<Er> page, ErDto form) {
		User user = (User) authentication.getPrincipal();
		Page<ErDto> data = iErService.findPage(page, user, form);
		return Datagrid.of(data);
	}

	/**
	 * 更新
	 * 
	 * @param authentication
	 * @param form
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/update")
	public Result<?> ajaxUpdate(Authentication authentication, ErDto form) {
		try {
			User user = (User) authentication.getPrincipal();
			Er entity = iErService.getOne(Wrappers.<Er>lambdaQuery().eq(Er::getUuid, form.getUuid()));
			entity.setUpdatedAt(new Date());
			entity.setUpdatorUserId(user.getUserId());
			entity.setUpdatorNickname(user.getNickname());
			iErService.updateById(entity);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
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
	@PostMapping("/ajax/createGraph")
	public Result<?> createGraph(Authentication authentication, String name, String uuids) {
		try {
			User user = (User) authentication.getPrincipal();
			List<String> uuidList = StrUtil.splitTrim(uuids, StrUtil.COMMA);
			List<ErDto> ers = iErService.findListWithFields(uuidList);
			eRModelParser.create(user, name, ers);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}