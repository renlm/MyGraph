package cn.renlm.graph.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 请求认证检查
 * 
 * @author Renlm
 *
 */
@Component
@RequiredArgsConstructor
public class RequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	public static final Map<RequestMatcher, Collection<String>> REQUEST_MATCHER_MAP;

	static {
		REQUEST_MATCHER_MAP = Collections.synchronizedMap(new HashMap<>());
	}

	private final UserService userService;

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		Authentication info = authentication.get();

		if (info == null) {
			return new AuthorizationDecision(false);
		}

		if (BooleanUtil.isFalse(info.isAuthenticated())) {
			return new AuthorizationDecision(false);
		}

		if (MapUtil.isEmpty(REQUEST_MATCHER_MAP)) {
			userService.loadRequestMatcher(REQUEST_MATCHER_MAP);
		}

		boolean granted = true;
		List<String> codes = info.getAuthorities().stream().map(it -> it.getAuthority()).collect(Collectors.toList());
		HttpServletRequest httpServletRequest = object.getRequest();
		for (Map.Entry<RequestMatcher, Collection<String>> entry : REQUEST_MATCHER_MAP.entrySet()) {
			if (entry.getKey().matches(httpServletRequest)) {
				granted = false;
				Collection<String> authorities = entry.getValue();
				if (CollUtil.containsAny(codes, authorities)) {
					return new AuthorizationDecision(true);
				}
			}
		}

		return new AuthorizationDecision(granted);
	}

}
