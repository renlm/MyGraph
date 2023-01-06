package cn.renlm.graph.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.util.SessionUtil;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.plugins.MyResponse.StatusCode;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户信息接口
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/api/user")
public class UserApiController {

	/**
	 * 获取会话秘钥（AES/CBC/PKCS5Padding）
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getAesKey")
	public Result<String> getAesKey(HttpServletRequest request) {
		String aesKey = SessionUtil.getAesKey(request);
		return Result.success(aesKey);
	}

	/**
	 * 获取当前登录用户
	 * 
	 * @param request
	 * @param authentication
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getCurrent")
	public Result<User> getCurrent(HttpServletRequest request, Authentication authentication) {
		if (authentication == null) {
			return Result.of(StatusCode.UNAUTHORIZED, "未登录");
		} else {
			User user = (User) authentication.getPrincipal();
			return Result.success(user);
		}
	}

}
