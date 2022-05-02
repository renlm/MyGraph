package cn.renlm.graph.modular.sys.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.mapper.SysUserMapper;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;
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

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	@Override
	public User loadUserByUsername(String username) {
		SysUser sysUser = this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
		if (sysUser == null) {
			throw new UsernameNotFoundException("Not Found By Username.");
		}
		User user = BeanUtil.copyProperties(sysUser, User.class);

		List<SysRole> roles = iSysRoleService.findListByUser(sysUser.getUserId());
		List<SysResource> resources = iSysResourceService.findListByUser(sysUser.getUserId());
		List<SysRoleResource> roleResources = iSysRoleResourceService.findListByUser(sysUser.getUserId());

		Map<Long, SysResource> srMap = new LinkedHashMap<>();
		List<GrantedAuthority> authorities = CollUtil.newArrayList();
		if (CollUtil.isNotEmpty(roles)) {
			roles.forEach(it -> {
				authorities.add(new SimpleGrantedAuthority(Role.HAS_ROLE_PREFIX + it.getCode()));
			});
		}
		if (CollUtil.isNotEmpty(resources)) {
			resources.forEach(it -> {
				srMap.put(it.getId(), it);
				authorities.add(new SimpleGrantedAuthority(it.getCode()));
			});
		}
		if (CollUtil.isNotEmpty(roleResources)) {
			for (SysRoleResource srr : roleResources) {
				SysResource sr = srMap.get(srr.getSysResourceId());
				if (sr != null) {
					sr.setText(ObjectUtil.defaultIfBlank(srr.getAlias(), sr.getText()));
					sr.setSort(ObjectUtil.defaultIfNull(srr.getSort(), sr.getSort()));
					sr.setDefaultHomePage(ObjectUtil.defaultIfNull(srr.getDefaultHomePage(), sr.getDefaultHomePage()));
				}
			}
			CollUtil.sort(resources, (o1, o2) -> {
				if (NumberUtil.equals(o1.getLevel(), o2.getLevel())) {
					if (NumberUtil.equals(o1.getSort(), o2.getSort())) {
						return -1;
					} else {
						return o1.getSort() - o2.getSort();
					}
				} else {
					return o1.getLevel() - o2.getLevel();
				}
			});
		}
		user.setRoles(roles);
		user.setResources(resources);
		return user.setAuthorities(authorities);
	}

	@Override
	@Transactional
	public void doModifyPersonal(Long id, SysUser form) {
		SysUser entity = this.getById(id);
		entity.setNickname(form.getNickname());
		entity.setRealname(form.getRealname());
		entity.setSex(form.getSex());
		entity.setBirthday(form.getBirthday());
		entity.setMobile(form.getMobile());
		entity.setEmail(form.getEmail());
		entity.setSign(form.getSign());
		entity.setAvatar(form.getAvatar());
		entity.setRemark(form.getRemark());
		entity.setUpdatedAt(new Date());
		this.updateById(entity);
	}
}
