package cn.renlm.graph.controller.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.common.Role;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyLogDto;
import cn.renlm.graph.modular.gateway.dto.GatewayStatisticalDataDto;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyLogService;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;

/**
 * 网关代理日志
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/gateway/proxyLog")
public class GatewayProxyLogController {

	@Autowired
	private IGatewayProxyLogService iGatewayProxyLogService;

	/**
	 * 日志主页
	 * 
	 * @param model
	 * @param proxyConfigUuid
	 * @return
	 */
	@GetMapping
	@PreAuthorize(Role.AdminSpEL)
	public String index(ModelMap model, String proxyConfigUuid) {
		model.put("proxyConfigUuid", proxyConfigUuid);
		return "gateway/proxyLog";
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
	@PreAuthorize(Role.AdminSpEL)
	public Datagrid<GatewayProxyLog> page(Authentication authentication, Page<GatewayProxyLog> page,
			GatewayProxyLogDto form) {
		User user = (User) authentication.getPrincipal();
		Page<GatewayProxyLog> data = iGatewayProxyLogService.findPage(page, user, form);
		return Datagrid.of(data);
	}

	/**
	 * 统计数据
	 * 
	 * @param model
	 * @param proxyConfigUuid
	 * @return
	 */
	@GetMapping("/statisticalData")
	@PreAuthorize(Role.AdminSpEL)
	public String statisticalData(ModelMap model, String proxyConfigUuid) {
		model.put("proxyConfigUuid", proxyConfigUuid);
		return "gateway/statisticalData";
	}

	/**
	 * 统计数据
	 * 
	 * @param proxyConfigUuid
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/getStatisticalData")
	@PreAuthorize(Role.AdminSpEL)
	public Result<GatewayStatisticalDataDto> ajaxGetStatisticalData(String proxyConfigUuid) {
		GatewayStatisticalDataDto data = iGatewayProxyLogService.getStatisticalData(proxyConfigUuid);
		return Result.success(data);
	}
}