package cn.renlm.graph.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.security.UserService;

/**
 * 用户信息接口
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/api/user")
public class UserApiController {

	@Autowired
	private UserService userService;

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
		User refreshUser = userService.refreshAuthentication();
		refreshUser.setPassword(null);
		return Result.success(refreshUser);
	}
}