package cn.renlm.graph.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Resource;
import cn.renlm.graph.modular.sys.dto.SysOrgDto;
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
	 * 登录凭证
	 */
	private String ticket;

	/**
	 * 角色列表
	 */
	private List<SysRole> roles;

	/**
	 * 资源列表
	 */
	private List<SysResource> resources;

	/**
	 * 组织机构列表
	 */
	private List<SysOrgDto> orgs;

	/**
	 * 权限列表
	 */
	private List<GrantedAuthority> authorities;

	/**
	 * 获取默认主页
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<SysResource> getHomePages() {
		if (CollUtil.isEmpty(this.getResources())) {
			return CollUtil.newArrayList();
		}
		Resource.Type[] types = { Resource.Type.menu, Resource.Type.urlInsidePage, Resource.Type.urlNewWindows,
				Resource.Type.permission };
		List<SysResource> list = this.getResources().stream().filter(r -> BooleanUtil.isTrue(r.getDefaultHomePage()))
				.filter(r -> StrUtil.isNotBlank(r.getUrl())).filter(r -> {
					for (Resource.Type type : types) {
						if (type.name().equals(r.getResourceTypeCode())) {
							return true;
						}
					}
					return false;
				}).collect(Collectors.toList());
		CollUtil.sort(list, (o1, o2) -> {
			if (NumberUtil.equals(o1.getSort(), o2.getSort())) {
				return -1;
			} else {
				return o1.getSort() - o2.getSort();
			}
		});
		return list;
	}

	/**
	 * 获取首页模块导航树
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<Tree<Long>> getNavGroup() {
		if (CollUtil.isEmpty(this.getResources())) {
			return CollUtil.newArrayList();
		}
		Resource.Type[] types = { Resource.Type.menu, Resource.Type.urlInsidePage, Resource.Type.urlNewWindows,
				Resource.Type.more };
		List<SysResource> list = this.getResources().stream().filter(r -> {
			for (Resource.Type type : types) {
				if (type.name().equals(r.getResourceTypeCode())) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		return TreeUtil.build(list, null, (object, treeNode) -> {
			BeanUtil.copyProperties(object, treeNode);
			treeNode.setId(object.getId());
			treeNode.setName(object.getText());
			treeNode.setWeight(object.getSort());
			treeNode.setParentId(object.getPid());
		});
	}

	/**
	 * 获取菜单树
	 * 
	 * @param parentUuid
	 * @return
	 */
	public List<Tree<Long>> getMenuTree(String parentUuid) {
		if (CollUtil.isEmpty(this.getResources()) || StrUtil.isBlank(parentUuid)) {
			return CollUtil.newArrayList();
		}
		SysResource parent = new SysResource();
		Resource.Type[] types = { Resource.Type.menu, Resource.Type.urlInsidePage, Resource.Type.urlNewWindows,
				Resource.Type.markdown };
		List<SysResource> list = this.getResources().stream().filter(r -> {
			if (StrUtil.equals(parentUuid, r.getResourceId())) {
				BeanUtil.copyProperties(r, parent);
			}
			for (Resource.Type type : types) {
				if (type.name().equals(r.getResourceTypeCode())) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		if (parent.getId() == null) {
			return CollUtil.newArrayList();
		}
		return TreeUtil.build(list, parent.getId(), (object, treeNode) -> {
			BeanUtil.copyProperties(object, treeNode);
			treeNode.setId(object.getId());
			treeNode.setName(object.getText());
			treeNode.setWeight(object.getSort());
			treeNode.setParentId(object.getPid());
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