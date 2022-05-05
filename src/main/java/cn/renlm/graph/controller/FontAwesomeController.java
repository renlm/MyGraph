package cn.renlm.graph.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.response.Result;

/**
 * Font Awesome
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/fontAwesome")
public class FontAwesomeController {

	/**
	 * 图标集
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/icons")
	public Result<List<?>> icons() {
		return Result.success();
	}
}