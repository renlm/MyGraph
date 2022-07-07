package cn.renlm.graph.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import cn.renlm.graph.security.DynamicAccessDecisionVoter;
import cn.renlm.graph.security.DynamicFilterInvocationSecurityMetadataSource;
import cn.renlm.graph.security.MyAuthenticationFailureHandler;
import cn.renlm.graph.security.MyAuthenticationSuccessHandler;
import cn.renlm.graph.security.MyDaoAuthenticationProvider;
import cn.renlm.graph.security.MyWebAuthenticationDetails;
import cn.renlm.graph.security.TicketAuthenticationFilter;
import cn.renlm.graph.security.UserService;
import cn.renlm.graph.util.GatewayUtil;

/**
 * 安全框架
 * 
 * @author Renlm
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
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
	 * 验证码图片匹配路径
	 */
	public static final String KaptchaAntMatcher = "/kaptcha";

	/**
	 * 登录接口
	 */
	public static final String LoginProcessingUrl = "/dologin";

	/**
	 * 开放接口匹配路径
	 */
	public static final String APIAntMatcher = "/api/**";

	/**
	 * 开放网页匹配路径
	 */
	public static final String PubAntMatcher = "/pub/**";

	/**
	 * 网关代理匹配路径
	 */
	public static final String GwAntMatcher = GatewayUtil.proxyPath + "**";

	/**
	 * 白名单
	 */
	public static final String[] WHITE_LIST = {
			KaptchaAntMatcher,
			APIAntMatcher,
			PubAntMatcher,
			GwAntMatcher,
			LoginPage, 
			logoutUrl, 
			LoginProcessingUrl 
		};

	/**
	 * 静态资源
	 */
	public static final String[] STATIC_PATHS = { 
			"/favicon.ico",
			"/static/**",
			"/webjars/**" 
		};
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

	@Autowired
	private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

	@Autowired
	private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

	@Autowired
	private TicketAuthenticationFilter ticketAuthenticationFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 认证过滤器（Ticket）
		http.addFilterBefore(ticketAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		// 启用csrf
		http.csrf()
			.ignoringAntMatchers(APIAntMatcher)
			.ignoringAntMatchers(PubAntMatcher)
			.ignoringAntMatchers(GwAntMatcher)
			.ignoringAntMatchers(LoginProcessingUrl)
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		// 会话
		http.sessionManagement()
			.invalidSessionUrl(LoginPage)
			.maximumSessions(100 * 100 * 10)
			.expiredUrl(LoginPage)
			.sessionRegistry(sessionRegistry());
		// 资源访问控制
		http.authorizeRequests()
				// 放行所有OPTIONS请求
				.antMatchers(HttpMethod.OPTIONS)
					.permitAll()
				// 白名单
				.antMatchers(WHITE_LIST)
					.permitAll()
				// 登录访问限制
				.anyRequest()
					.authenticated()
					.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
						@Override
						public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
							fsi.setSecurityMetadataSource(
									dynamicFilterInvocationSecurityMetadataSource(fsi.getSecurityMetadataSource()));
							fsi.setAccessDecisionManager(accessDecisionManager());
							return fsi;
						}
					})
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
					.failureHandler(myAuthenticationFailureHandler)
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
	 * 控制会话并发的会话注册表
	 * 
	 * @return
	 */
	@Bean
	public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
		return new SpringSessionBackedSessionRegistry<>(sessionRepository);
	}
	
	/**
	 * 鉴权处理
	 * 
	 * @return
	 */
	@Bean
	public AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
		decisionVoters.add(new AuthenticatedVoter());
		decisionVoters.add(new WebExpressionVoter());
		decisionVoters.add(new DynamicAccessDecisionVoter());
		return new UnanimousBased(decisionVoters);
	}

	/**
	 * 权限加载
	 * 
	 * @param filterInvocationSecurityMetadataSource
	 * @return
	 */
	@Bean
	public DynamicFilterInvocationSecurityMetadataSource dynamicFilterInvocationSecurityMetadataSource(
			FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) {
		return new DynamicFilterInvocationSecurityMetadataSource(userService, filterInvocationSecurityMetadataSource);
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