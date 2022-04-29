package cn.renlm.graph.modular.sys.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.mapper.SysUserMapper;
import cn.renlm.graph.modular.sys.service.ISysUserService;
import cn.renlm.graph.dto.User;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
	
	@Override
	public User loadUserByUsername(String username) {
		SysUser sysUser = this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
		if (sysUser == null) {
			throw new UsernameNotFoundException("Not Found By Username.");
		}
		User user = BeanUtil.copyProperties(sysUser, User.class);
		List<SysRole> roles = CollUtil.newArrayList();
		List<Long> sysRoleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
		List<SysResource> resources = CollUtil.newArrayList();
		List<GrantedAuthority> authorities = CollUtil.newArrayList();
		if (CollUtil.isNotEmpty(roles)) {
			roles.forEach(it -> {
				authorities.add(new SimpleGrantedAuthority(Role.HAS_ROLE_PREFIX + it.getCode()));
			});
		}
		if (CollUtil.isNotEmpty(resources)) {
			resources.forEach(it -> {
				authorities.add(new SimpleGrantedAuthority(it.getCode()));
			});
		}
		return user.setAuthorities(authorities);
	}
}
