package cn.renlm.graph.modular.sys.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.mapper.SysUserMapper;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleService;
import cn.renlm.graph.modular.sys.service.ISysUserService;

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

	@Autowired
	private ISysRoleService iSysRoleService;

	@Autowired
	private ISysResourceService iSysResourceService;

	@Override
	public User loadUserByUsername(String username) {
		SysUser sysUser = this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
		if (sysUser == null) {
			throw new UsernameNotFoundException("Not Found By Username.");
		}
		User user = BeanUtil.copyProperties(sysUser, User.class);
		List<SysRole> roles = iSysRoleService.findList(sysUser.getUserId());
		List<SysResource> resources = iSysResourceService.findList(sysUser.getUserId());
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
