package cn.renlm.graph.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.response.Layui;

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
	 * 初始化
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/init")
	public Layui<?> init() {
		return Layui.success(null);
	}

	/**
	 * 查看群员
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/members")
	public Layui<?> members() {
		return Layui.success(null);
	}

	/**
	 * 上传图片
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/uploadImage")
	public Layui<?> uploadImage() {
		return Layui.success(null);
	}

	/**
	 * 上传文件
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/uploadFile")
	public Layui<?> uploadFile() {
		return Layui.success(null);
	}
}