package cn.renlm.graph.controller;

import java.awt.Color;
import java.awt.Image;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.RandomUtil;
import cn.renlm.graph.common.ConstVal;
import cn.renlm.graph.modular.sys.service.ISysConstService;
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

	@Autowired
	private ISysConstService iSysConstService;

	/**
	 * 图片
	 * 
	 * @param request
	 * @param response
	 */
	@GetMapping
	@SneakyThrows
	public void image(HttpServletRequest request, HttpServletResponse response) {
		int width = 0;
		int height = 0;
		if (iSysConstService.getCfgEnableRegistration()) {
			width = 108;
			height = 38;
		} else {
			width = 80;
			height = 38;
		}
		int codeCount = 3;
		String code = RandomUtil.randomNumbers(codeCount);
		LineCaptcha captcha = CaptchaUtil.createLineCaptcha(width, height, codeCount, 24);
		captcha.setBackground(new Color(245, 245, 245));
		captcha.setTextAlpha(0.98F);
		Image image = captcha.createImage(code);
		request.getSession().setAttribute(ConstVal.CAPTCHA_SESSION_KEY, code);
		@Cleanup
		ServletOutputStream out = response.getOutputStream();
		ImgUtil.write(image, ImgUtil.IMAGE_TYPE_PNG, out);
	}
}