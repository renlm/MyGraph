package cn.renlm.graph.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.anji.captcha.config.AjCaptchaServiceAutoConfiguration;
import com.anji.captcha.config.AjCaptchaStorageAutoConfiguration;
import com.anji.captcha.properties.AjCaptchaProperties;

/**
 * 验证码
 * 
 * @author Renlm
 *
 */
@Configuration
@EnableConfigurationProperties(AjCaptchaProperties.class)
@Import({ AjCaptchaServiceAutoConfiguration.class, AjCaptchaStorageAutoConfiguration.class })
public class AjCaptchaAutoConfig {

}
