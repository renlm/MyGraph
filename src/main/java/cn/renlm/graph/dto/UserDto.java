package cn.renlm.graph.dto;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import cn.hutool.core.util.BooleanUtil;
import cn.renlm.graph.entity.Users;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDto extends Users implements org.springframework.security.core.userdetails.UserDetails {

	private static final long serialVersionUID = 1L;

	private String token;

	private Date expiryTime;

	private List<GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return BooleanUtil.isFalse(this.getDisabled());
	}
}