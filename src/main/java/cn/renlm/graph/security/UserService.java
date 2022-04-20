package cn.renlm.graph.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Roles;
import cn.renlm.graph.dto.UserDto;
import cn.renlm.graph.entity.Users;
import cn.renlm.graph.service.IUsersService;

/**
 * 用户信息
 * 
 * @author Renlm
 *
 */
@Service
public class UserService implements UserDetailsService {

	@Autowired
	private IUsersService iUsersService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = iUsersService.getOne(Wrappers.<Users>lambdaQuery().func(wrapper -> {
			wrapper.eq(Users::getUsername, username);
		}));
		if (user == null) {
			throw new UsernameNotFoundException("Not Found By Username.");
		}
		UserDto userDetails = BeanUtil.copyProperties(user, UserDto.class);
		List<GrantedAuthority> authorities = CollUtil.newArrayList();
		userDetails.setToken(IdUtil.simpleUUID().toUpperCase());
		userDetails.setAuthorities(authorities);
		if (StrUtil.isNotBlank(user.getRole())) {
			GrantedAuthority authority = new SimpleGrantedAuthority(Roles.HAS_ROLE_PREFIX + user.getRole());
			authorities.add(authority);
		}
		return userDetails;
	}
}