package cn.renlm.mygraph.security;

import static cn.hutool.core.text.CharSequenceUtil.EMPTY;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.sys.entity.SysResource;
import cn.renlm.mygraph.modular.sys.service.ISysResourceService;
import cn.renlm.mygraph.modular.sys.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Service
public class UserService implements UserDetailsService {

	@Autowired
	private SecurityContextRepository securityContextRepository;

	@Autowired
	private ISysUserService iSysUserService;

	@Autowired
	private ISysResourceService iSysResourceService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userDetails = iSysUserService.loadUserByUsername(username);
		return userDetails;
	}

	/**
	 * 刷新当前登录用户信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public User refreshUserDetails(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = (Authentication) request.getUserPrincipal();
		User principal = (User) authentication.getPrincipal();
		User user = iSysUserService.loadUserByUsername(principal.getUsername());
		user.setPassword(EMPTY);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(user, EMPTY, user.getAuthorities()));
		securityContextRepository.saveContext(context, request, response);
		return user;
	}

	/**
	 * 加载请求认证权限
	 * 
	 * @param requestMatcherMap
	 */
	public void loadRequestMatcher(final Map<RequestMatcher, Collection<String>> requestMatcherMap) {
		requestMatcherMap.clear();
		Map<String, Collection<String>> map = new LinkedHashMap<>();
		List<SysResource> list = iSysResourceService.list();
		for (SysResource item : list) {
			if (StrUtil.isBlank(item.getUrl())) {
				return;
			}
			if (BooleanUtil.isTrue(item.getDeleted())) {
				return;
			}
			if (BooleanUtil.isTrue(item.getDisabled())) {
				return;
			}
			Collection<String> authorities = map.get(item.getUrl());
			if (CollUtil.isEmpty(authorities)) {
				authorities = CollUtil.newArrayList();
				authorities.add(item.getCode());
				map.put(item.getUrl(), authorities);
			} else {
				authorities.add(item.getCode());
			}
		}
		map.forEach((pattern, authority) -> {
			requestMatcherMap.put(new AntPathRequestMatcher(pattern), authority);
		});
	}

}
