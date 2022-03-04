package cn.renlm.graph.controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
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
		ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(136, 50, 3, 0);
		request.getSession().setAttribute(ConstVal.CAPTCHA_SESSION_KEY, captcha.getCode());
		@Cleanup
		ServletOutputStream out = response.getOutputStream();
		captcha.write(out);
	}
}