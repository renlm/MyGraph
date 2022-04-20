package cn.renlm.graph.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Profiles;
import cn.renlm.graph.common.Roles;
import cn.renlm.graph.security.MyAuthenticationSuccessHandler;
import cn.renlm.graph.security.MyDaoAuthenticationProvider;
import cn.renlm.graph.security.MyWebAuthenticationDetails;
import cn.renlm.graph.security.UserService;

/**
 * 安全框架
 * 
 * @author Renlm
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
	public static final String LoginProcessingUrl = "/dologin";

	/**
	 * 开放接口匹配路径
	 */
	public static final String APIAntMatcher = "/api/**";

	/**
	 * 白名单
	 */
	public static final String[] WHITE_LIST = {  
			"/captcha",
			"/register",
			"/doRegister",
			APIAntMatcher,
			LoginPage, 
			logoutUrl, 
			LoginProcessingUrl 
		};

	/**
	 * 静态资源
	 */
	public static final String[] STATIC_PATHS = { 
			"/favicon.ico",
			"/static/favicon.ico",
			"/static/{path:^((?!editor.md).)*$}/**", 
			"/webjars/**" 
		};
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 在代理服务器后面运行时启用 Https
		if(StrUtil.containsAny(Profiles.prod.name(), environment.getActiveProfiles())) {
			http.requiresChannel().anyRequest().requiresSecure();
		}
		// 启用csrf
		http.csrf()
			.ignoringAntMatchers(APIAntMatcher)
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		// 资源访问控制
		http.authorizeRequests()
				// 放行所有OPTIONS请求
				.antMatchers(HttpMethod.OPTIONS)
					.permitAll()
				// 白名单
				.antMatchers(WHITE_LIST)
					.permitAll()
				// 访问控制
				.antMatchers("/static/editor.md/**")
					.hasAnyRole(Roles.admin.name(), Roles.user.name())
				// 登录访问限制
				.anyRequest()
					.authenticated()
				// Iframe同源访问
				.and()
					.headers()
					.frameOptions()
					.sameOrigin()
				// 登录
				.and()
					.formLogin()
					.loginPage(LoginPage)
					.loginProcessingUrl(LoginProcessingUrl)
					.authenticationDetailsSource(authenticationDetailsSource())
					.successHandler(myAuthenticationSuccessHandler)
				// 注销
				.and()
					.logout()
					.logoutUrl(logoutUrl);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(STATIC_PATHS);
	}
	
	/**
	 * 登录认证
	 * 
	 * @param messageSource
	 * @return
	 */
	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider(MessageSource messageSource) {
		return new MyDaoAuthenticationProvider(userService, new BCryptPasswordEncoder(), messageSource);
	}

	/**
	 * 附加信息
	 * 
	 * @return
	 */
	@Bean
	AuthenticationDetailsSource<HttpServletRequest, MyWebAuthenticationDetails> authenticationDetailsSource() {
		return new AuthenticationDetailsSource<HttpServletRequest, MyWebAuthenticationDetails>() {
			@Override
			public MyWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				return new MyWebAuthenticationDetails(context);
			}
		};
	}
}