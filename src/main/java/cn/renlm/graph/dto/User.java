package cn.renlm.graph.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户信息
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class User extends SysUser implements org.springframework.security.core.userdetails.UserDetails {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色列表
	 */
	private List<SysRole> roles;

	/**
	 * 资源列表
	 */
	private List<SysResource> resources;

	/**
	 * 权限列表
	 */
	private List<GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return getAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return getAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return getCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return getEnabled();
	}
}