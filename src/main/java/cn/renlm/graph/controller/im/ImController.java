package cn.renlm.graph.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 即时通讯
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/im")
public class ImController {

	/**
	 * 帮助中心
	 * 
	 * @return
	 */
	@GetMapping
	public String index() {
		return "im/index";
	}
}