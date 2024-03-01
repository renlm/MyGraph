package cn.renlm.graph.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 验证码
 * 
 * @author RenLiMing(任黎明)
 *
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

	@Autowired
	private CaptchaService captchaService;

	@PostMapping("/get")
	public ResponseModel get(@RequestBody CaptchaVO data, HttpServletRequest request) {
		data.setBrowserInfo(WebUtils.getSessionId(request));
		return captchaService.get(data);
	}

	@PostMapping("/check")
	public ResponseModel check(@RequestBody CaptchaVO data, HttpServletRequest request) {
		data.setBrowserInfo(WebUtils.getSessionId(request));
		return captchaService.check(data);
	}

}
