package cn.renlm.graph.config;

import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import cn.renlm.graph.security.RequestAuthorizationManager;
import cn.renlm.graph.security.WebAuthenticationDetails;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Security 配置
 * 
 * @author Renlm
 *
 */
@Configuration
@EnableWebSecurity
@EnableRedisIndexedHttpSession
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

	/**
	 * 登录页
	 */
	public static final String LoginPage = "/login";

	/**
	 * 退出地址
	 */
	public static final String logoutUrl = "/logout";

	/**
	 * 登录接口
	 */
	public static final String LoginProcessingUrl = "/doLogin";

	/**
	 * 验证码匹配路径
	 */
	public static final String CaptchaAntMatcher = "/captcha/**";

	/**
	 * 开放接口匹配路径
	 */
	public static final String APIAntMatcher = "/api/**";

	/**
	 * 开放网页匹配路径
	 */
	public static final String PubAntMatcher = "/pub/**";

	/**
	 * 白名单
	 */
	public static final String[] WHITE_LIST = {
			LoginPage, 
			logoutUrl, 
			LoginProcessingUrl, 
			CaptchaAntMatcher, 
			APIAntMatcher, 
			PubAntMatcher
		};

	/**
	 * 静态资源
	 */
	public static final String[] STATIC_PATHS = { 
			"/favicon.ico", 
			"/static/**", 
			"/webjars/**" 
		};

	@Resource
	private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RequestAuthorizationManager authorizationManager)
			throws Exception {
		// 启用Csrf
		http.csrf()
			.ignoringRequestMatchers(APIAntMatcher)
			.ignoringRequestMatchers(PubAntMatcher)
			.ignoringRequestMatchers(CaptchaAntMatcher)
			.ignoringRequestMatchers(LoginProcessingUrl)
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		// 会话
		http.sessionManagement()
			.invalidSessionUrl(LoginPage)
			.maximumSessions(1000)
			.expiredUrl(LoginPage)
			.sessionRegistry(sessionRegistry());
		// 资源访问控制
		http.authorizeHttpRequests()
			// 放行所有OPTIONS请求
			.requestMatchers(HttpMethod.OPTIONS).permitAll()
			// 白名单
			.requestMatchers(WHITE_LIST).permitAll()
			// 静态资源
			.requestMatchers(STATIC_PATHS).permitAll()
			// 请求访问限制
			.anyRequest().access(authorizationManager);
		// Iframe同源访问
		http.headers()
			.frameOptions()
			.sameOrigin();
		// 登录
		http.formLogin()
			.loginPage(LoginPage)
			.loginProcessingUrl(LoginProcessingUrl)
			.authenticationDetailsSource(authenticationDetailsSource());
		// 注销
		http.logout()
			.logoutUrl(logoutUrl)
			.invalidateHttpSession(true);
		return http.build();
	}

	/**
	 * 会话并发
	 * 
	 * @return
	 */
	@Bean
	SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
		return new SpringSessionBackedSessionRegistry<>(sessionRepository);
	}

	/**
	 * 附加信息
	 * 
	 * @return
	 */
	@Bean
	AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
		return new AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails>() {
			@Override
			public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
				return new WebAuthenticationDetails(context);
			}
		};
	}

}
