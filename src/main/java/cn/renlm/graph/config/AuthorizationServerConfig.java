package cn.renlm.graph.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.dto.UserBase;
import cn.renlm.graph.properties.KeyStoreProperties;
import cn.renlm.graph.security.OAuth2PasswordAuthenticationConverter;
import cn.renlm.graph.security.OAuth2PasswordAuthenticationProvider;

/**
 * 认证服务
 * 
 * @author Renlm
 *
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		OAuth2AuthorizationServerConfigurer oAuth2Configurer = http
				.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
		oAuth2Configurer.oidc(Customizer.withDefaults());
		http.exceptionHandling(exceptions -> {
			exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(WebSecurityConfig.LoginPage));
		});
		oAuth2Configurer.tokenEndpoint(tokenEndpoint -> {
			tokenEndpoint.accessTokenRequestConverters(accessTokenRequestConvertersConsumer -> {
				accessTokenRequestConvertersConsumer.add(new OAuth2PasswordAuthenticationConverter());
			});
		});
		Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
			OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
			JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
			return new OidcUserInfo(principal.getToken().getClaims());
		};
		oAuth2Configurer.oidc((oidc) -> oidc.userInfoEndpoint((userInfo) -> userInfo.userInfoMapper(userInfoMapper)));
		http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));
		SecurityFilterChain securityFilterChain = http.build();
		this.addCustomOAuth2GrantAuthenticationProvider(http);
		return securityFilterChain;
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
		JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
		return registeredClientRepository;
	}

	@Bean
	public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
	}

	@Bean
	public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource(KeyStoreProperties keyStoreProperties) {
		JWKSet jwkSet = new JWKSet(keyStoreProperties.getRSAKey());
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	/**
	 * 令牌增强（附加信息）
	 * 
	 * @return
	 */
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
		return (context) -> {
			if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())
					|| OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
				context.getClaims().claims(claim -> {
					Authentication authentication = context.getPrincipal();
					Object principal = authentication.getPrincipal();
					if (principal instanceof User) {
						User userDetails = (User) authentication.getPrincipal();
						claim.put("principal", UserBase.of(userDetails));
						claim.put("authorities", userDetails.getAuthorities());
					}
				});
			}
		};
	}

	/**
	 * 自定义授权认证
	 * 
	 * @param http
	 */
	private void addCustomOAuth2GrantAuthenticationProvider(HttpSecurity http) {
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
		OAuth2TokenGenerator<?> tokenGenerator = http.getSharedObject(OAuth2TokenGenerator.class);
		http.authenticationProvider(
				new OAuth2PasswordAuthenticationProvider(authenticationManager, authorizationService, tokenGenerator));
	}

}
