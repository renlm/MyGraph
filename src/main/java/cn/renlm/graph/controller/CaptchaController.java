package cn.renlm.graph.controller;

import java.awt.Image;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.RandomUtil;
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
@RequestMapping("/captcha")
public class CaptchaController {

	/**
	 * 图片
	 * 
	 * @param request
	 * @param response
	 */
	@SneakyThrows
	@RequestMapping
	public void image(HttpServletRequest request, HttpServletResponse response) {
		int codeCount = 3;
		String code = RandomUtil.randomNumbers(codeCount);
		LineCaptcha captcha = CaptchaUtil.createLineCaptcha(108, 38, codeCount, 24);
		Image image = captcha.createImage(code);
		request.getSession().setAttribute(ConstVal.CAPTCHA_SESSION_KEY, code);
		@Cleanup
		ServletOutputStream out = response.getOutputStream();
		ImgUtil.write(image, ImgUtil.IMAGE_TYPE_PNG, out);
	}
}