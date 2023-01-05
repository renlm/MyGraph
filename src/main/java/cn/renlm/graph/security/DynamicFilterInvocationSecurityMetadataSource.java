package cn.renlm.graph.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.hutool.core.collection.CollUtil;
import cn.renlm.graph.config.WebSecurityConfig;

/**
 * 权限加载
 * 
 * @author Renlm
 *
 */
public class DynamicFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	public static final Collection<ConfigAttribute> allConfigAttributes = Collections
			.synchronizedList(new ArrayList<>());
	private static final Map<String, Collection<ConfigAttribute>> requestMap = Collections
			.synchronizedMap(new HashMap<>());
	private static final Map<RequestMatcher, Collection<ConfigAttribute>> matcherMap = Collections
			.synchronizedMap(new HashMap<>());
	private static final List<RequestMatcher> ignoreMatchers = Collections.synchronizedList(new ArrayList<>());
	static {
		ignoreMatchers.add(new AntPathRequestMatcher(WebSecurityConfig.LoginPage));
		for (String ignore : WebSecurityConfig.WHITE_LIST) {
			ignoreMatchers.add(new AntPathRequestMatcher(ignore));
		}
	}

	private UserService userService;

	private FilterInvocationSecurityMetadataSource superMetadataSource;

	public DynamicFilterInvocationSecurityMetadataSource(UserService userService,
			FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) {
		this.userService = userService;
		this.superMetadataSource = filterInvocationSecurityMetadataSource;
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if (CollUtil.isEmpty(allConfigAttributes)) {
			allConfigAttributes.addAll(userService.loadAllAuthority(requestMap, matcherMap));
		}
		FilterInvocation invocation = ((FilterInvocation) object);
		HttpServletRequest request = invocation.getRequest();
		for (RequestMatcher matcher : ignoreMatchers) {
			if (matcher.matches(request)) {
				return null;
			}
		}
		for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : matcherMap.entrySet()) {
			if (entry.getKey().matches(request)) {
				return entry.getValue();
			}
		}
		return superMetadataSource.getAttributes(object);
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return allConfigAttributes;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}
}