package cn.renlm.graph.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.SessionUtil;

/**
 * 退出登录
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/api/logout")
public class LogoutApiController {

	/**
	 * 退出登录
	 * 
	 * @param request
	 * @param ticket
	 * @return
	 */
	@PostMapping
	@ResponseBody
	public Result<?> logout(HttpServletRequest request, String ticket) {
		request.getSession().invalidate();
		SessionUtil.deleteSession(ticket);
		return Result.success();
	}
}