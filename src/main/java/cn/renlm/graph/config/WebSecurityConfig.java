package cn.renlm.graph.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
	public static final String LoginProcessingUrl = "/auth";

	/**
	 * 静态资源
	 */
	public static final String[] STATIC_PATHS = {"/static/**", "/webjars/**"};

	/**
	 * 白名单
	 */
	public static final String[] WHITE_LIST = {"/api/**", LoginPage, logoutUrl,
			LoginProcessingUrl};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 登录
		http.formLogin().loginPage(LoginPage)
				.loginProcessingUrl(LoginProcessingUrl);
		// 注销
		http.logout().logoutUrl(logoutUrl);
		// 启用csrf
		http.csrf().csrfTokenRepository(
				CookieCsrfTokenRepository.withHttpOnlyFalse());
		// 资源访问控制
		http.authorizeRequests()
				// 放行所有OPTIONS请求
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				// 白名单
				.antMatchers(WHITE_LIST).permitAll()
				// 登录访问限制
				.anyRequest().authenticated()
				// 默认表单登录
				.and().formLogin();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(STATIC_PATHS);
	}
}