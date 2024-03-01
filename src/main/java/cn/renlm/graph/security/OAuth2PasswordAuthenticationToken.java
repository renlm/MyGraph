package cn.renlm.graph.security;

import static cn.renlm.graph.security.OAuth2PasswordAuthenticationConverter.PASSWORD;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import lombok.Getter;

/**
 * 密码模式
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class OAuth2PasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

	private static final long serialVersionUID = 1L;

	@Getter
	private final Set<String> scopes;

	public OAuth2PasswordAuthenticationToken(Authentication clientPrincipal, @Nullable Set<String> scopes,
			@Nullable Map<String, Object> additionalParameters) {
		super(PASSWORD, clientPrincipal, additionalParameters);
		this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
	}

}
