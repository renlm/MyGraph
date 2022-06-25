package cn.renlm.graph.controller;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.code.kaptcha.Producer;

import cn.renlm.graph.common.ConstVal;
import lombok.Cleanup;
import lombok.SneakyThrows;

/**
 * 验证码
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping
public class CaptchaController {

	@Autowired
	private Producer producer;

	/**
	 * 图片
	 * 
	 * @param request
	 * @param response
	 */
	@SneakyThrows
	@GetMapping("/kaptcha")
	public void kaptcha(HttpServletRequest request, HttpServletResponse response) {
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("image/jpeg");
		String capText = producer.createText();
		request.getSession().setAttribute(ConstVal.CAPTCHA_SESSION_KEY, capText);
		BufferedImage bi = producer.createImage(capText);
		@Cleanup
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(bi, "jpeg", out);
	}
}