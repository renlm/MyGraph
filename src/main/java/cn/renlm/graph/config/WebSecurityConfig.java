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

import com.github.mkopylec.charon.configuration.GatewayUtil;

import cn.renlm.graph.security.DynamicAccessDecisionVoter;
import cn.renlm.graph.security.DynamicFilterInvocationSecurityMetadataSource;
import cn.renlm.graph.security.MyAuthenticationFailureHandler;
import cn.renlm.graph.security.MyAuthenticationSuccessHandler;
import cn.renlm.graph.security.MyDaoAuthenticationProvider;
import cn.renlm.graph.security.MyWebAuthenticationDetails;
import cn.renlm.graph.security.TicketAuthenticationFilter;
import cn.renlm.graph.security.UserService;

/**
 * ????????????
 * 
 * @author Renlm
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * ?????????
	 */
	public static final String LoginPage = "/login";

	/**
	 * ????????????
	 */
	public static final String logoutUrl = "/logout";

	/**
	 * ???????????????????????????
	 */
	public static final String KaptchaAntMatcher = "/kaptcha";

	/**
	 * ????????????
	 */
	public static final String LoginProcessingUrl = "/dologin";

	/**
	 * ????????????????????????
	 */
	public static final String APIAntMatcher = "/api/**";

	/**
	 * ????????????????????????
	 */
	public static final String PubAntMatcher = "/pub/**";

	/**
	 * ????????????????????????
	 */
	public static final String GwAntMatcher = GatewayUtil.proxyPath + "**";

	/**
	 * ?????????
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
	 * ????????????
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
		// ??????????????????Ticket???
		http.addFilterBefore(ticketAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		// ??????csrf
		http.csrf()
			.ignoringAntMatchers(APIAntMatcher)
			.ignoringAntMatchers(PubAntMatcher)
			.ignoringAntMatchers(GwAntMatcher)
			.ignoringAntMatchers(LoginProcessingUrl)
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		// ??????
		http.sessionManagement()
			.invalidSessionUrl(LoginPage)
			.maximumSessions(100 * 100 * 10)
			.expiredUrl(LoginPage)
			.sessionRegistry(sessionRegistry());
		// ??????????????????
		http.authorizeRequests()
				// ????????????OPTIONS??????
				.antMatchers(HttpMethod.OPTIONS)
					.permitAll()
				// ?????????
				.antMatchers(WHITE_LIST)
					.permitAll()
				// ??????????????????
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
				// Iframe????????????
				.and()
					.headers()
					.frameOptions()
					.sameOrigin()
				// ??????
				.and()
					.formLogin()
					.loginPage(LoginPage)
					.loginProcessingUrl(LoginProcessingUrl)
					.authenticationDetailsSource(authenticationDetailsSource())
					.successHandler(myAuthenticationSuccessHandler)
					.failureHandler(myAuthenticationFailureHandler)
				// ??????
				.and()
					.logout()
					.logoutUrl(logoutUrl);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(STATIC_PATHS);
	}
	
	/**
	 * ????????????????????????????????????
	 * 
	 * @return
	 */
	@Bean
	public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
		return new SpringSessionBackedSessionRegistry<>(sessionRepository);
	}
	
	/**
	 * ????????????
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
	 * ????????????
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
	 * ????????????
	 * 
	 * @param messageSource
	 * @return
	 */
	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider(MessageSource messageSource) {
		return new MyDaoAuthenticationProvider(userService, new BCryptPasswordEncoder(), messageSource);
	}

	/**
	 * ????????????
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