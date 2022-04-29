package cn.renlm.graph.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.common.Result;
import cn.renlm.graph.modular.sys.dto.User;

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
	 * 获取用户信息
	 * 
	 * @param authentication
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getInfo")
	public Result<User> getInfo(Authentication authentication) {
		if (authentication == null) {
			return Result.of(HttpStatus.UNAUTHORIZED);
		}
		User user = (User) authentication.getPrincipal();
		user.setPassword(null);
		return Result.success(user);
	}
}