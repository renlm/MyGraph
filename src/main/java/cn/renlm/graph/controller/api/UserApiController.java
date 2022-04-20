package cn.renlm.graph.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.UserDto;

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
	public Result<UserDto> getInfo(Authentication authentication) {
		UserDto user = (UserDto) authentication.getPrincipal();
		return Result.success(user);
	}
}