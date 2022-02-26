package com.lei.du.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lei.du.common.Result;

/**
 * 虚拟号码（AX模式，https://support.huaweicloud.com/api-PrivateNumber/privatenumber_02_0010.html）
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/api/ax")
public class APIAxController {

	/**
	 * 获取虚拟号码
	 * 
	 * @param origNum
	 * @param userData
	 * @return
	 */
	@ResponseBody
	@PostMapping("/getCalledNum")
	public Result getCalledNum(String origNum, String userData) {
		return Result.success();
	}
}