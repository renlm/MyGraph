package cn.renlm.graph.config;

import static com.google.code.kaptcha.Constants.KAPTCHA_BORDER;
import static com.google.code.kaptcha.Constants.KAPTCHA_IMAGE_HEIGHT;
import static com.google.code.kaptcha.Constants.KAPTCHA_IMAGE_WIDTH;
import static com.google.code.kaptcha.Constants.KAPTCHA_NOISE_COLOR;
import static com.google.code.kaptcha.Constants.KAPTCHA_NOISE_IMPL;
import static com.google.code.kaptcha.Constants.KAPTCHA_OBSCURIFICATOR_IMPL;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

/**
 * 验证码配置
 * 
 * @author Renlm
 *
 */
@Configuration
public class KaptchaConfig {
	
	@Bean
	public DefaultKaptcha getKaptchaBean() {
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		Properties properties = new Properties();
		properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_STRING, "0123456789");								// 验证码字符范围
		properties.setProperty(KAPTCHA_BORDER, "no");														// 图片边框
		properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");									// 字体颜色
		properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "3");										// 文字间隔
		properties.setProperty(KAPTCHA_IMAGE_WIDTH, "80");													// 图片宽度
		properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "38");													// 图片高度
		properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");										// 长度
		properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "宋体,楷体,微软雅黑");							// 字体
		properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "24"); 										// 字体大小
		properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy"); 	// 图片样式
		properties.setProperty(KAPTCHA_NOISE_COLOR, "255,96,0");											// 干扰颜色
		properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise"); 				// 去除干扰
		Config config = new Config(properties);
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}