package cn.renlm.graph.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
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

	/**
	 * 获取资源树
	 * 
	 * @param parentId
	 * @return
	 */
	public List<Tree<Long>> getResourceTree(Long parentId) {
		if (CollUtil.isEmpty(this.getResources())) {
			return CollUtil.newArrayList();
		}
		return TreeUtil.build(resources, null, (object, treeNode) -> {
			treeNode.setId(object.getId());
			treeNode.setName(object.getText());
			treeNode.setWeight(object.getSort());
			treeNode.setParentId(object.getPid());
			treeNode.putExtra("data", object);
		});
	}

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