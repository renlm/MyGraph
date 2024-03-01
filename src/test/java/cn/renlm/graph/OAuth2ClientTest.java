package cn.renlm.graph;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.test.context.ActiveProfiles;

import cn.hutool.core.lang.UUID;

/**
 * 注册客户端（注意启动时添加VM参数） 
-DKEY_STORE_PASSWORD=storepass 
-DKEY_STORE_ALIAS=alias
-DKEY_STORE_SECRET=keypass
 * 
 * @author RenLiMing(任黎明)
 *
 */
@ActiveProfiles("dev")
@SpringBootTest(classes = MyGraphApplication.class)
public class OAuth2ClientTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void test() {
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("test-client")
				.clientSecret("{noop}secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://127.0.0.1:7002/login/oauth2/code/test-client")
				.redirectUri("http://127.0.0.1:7002/authorized")
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				.clientSettings(ClientSettings.builder()
						.requireProofKey(false)
						.requireAuthorizationConsent(false)
						.build())
				.tokenSettings(TokenSettings.builder()
						.authorizationCodeTimeToLive(Duration.ofMinutes(5))
						.accessTokenTimeToLive(Duration.ofMinutes(5))
						.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
						.reuseRefreshTokens(true)
						.refreshTokenTimeToLive(Duration.ofMinutes(60))
						.idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
						.build())
				.build();
		JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
		registeredClientRepository.save(registeredClient);
	}

}
