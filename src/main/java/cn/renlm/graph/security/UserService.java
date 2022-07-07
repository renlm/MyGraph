package cn.renlm.graph.security;

import static cn.renlm.graph.common.ConstVal.PASSWORD_PARAM_NAME;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysUserService;

/**
 * 用户信息
 * 
 * @author Renlm
 *
 */
@Service
public class UserService implements UserDetailsService {

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
	 * @return
	 */
	public User refreshAuthentication() {
		Authentication authentication = getContext().getAuthentication();
		User principal = (User) authentication.getPrincipal();
		User user = iSysUserService.loadUserByUsername(principal.getUsername());
		BeanUtil.copyProperties(user, principal, PASSWORD_PARAM_NAME);
		getContext().setAuthentication(authentication);
		return principal;
	}

	/**
	 * 加载全部权限
	 * 
	 * @param requestMap
	 * @param matcherMap
	 * @return
	 */
	public Collection<ConfigAttribute> loadAllAuthority(final Map<String, Collection<ConfigAttribute>> requestMap,
			final Map<RequestMatcher, Collection<ConfigAttribute>> matcherMap) {
		List<SysResource> list = iSysResourceService.list();
		Collection<ConfigAttribute> attributes = CollUtil.newArrayList();
		list.forEach(it -> {
			String path = it.getUrl();
			ConfigAttribute attribute = new SecurityConfig(it.getCode());
			attributes.add(attribute);
			if (StrUtil.isBlank(path)) {
				return;
			}
			if (!requestMap.containsKey(path)) {
				requestMap.put(path, CollUtil.newArrayList());
			}
			requestMap.get(path).add(attribute);
		});
		for (Map.Entry<String, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
			matcherMap.put(new AntPathRequestMatcher(entry.getKey()), entry.getValue());
		}
		return attributes;
	}
}