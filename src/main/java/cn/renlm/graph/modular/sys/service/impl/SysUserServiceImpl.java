package cn.renlm.graph.modular.sys.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.modular.sys.dto.OrgChartDto;
import cn.renlm.graph.modular.sys.dto.ResourceDto;
import cn.renlm.graph.modular.sys.dto.User;
import cn.renlm.graph.modular.sys.dto.UserDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.mapper.SysUserMapper;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleService;
import cn.renlm.graph.modular.sys.service.ISysUserService;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

	@Autowired
	private ISysOrgService iSysOrgService;

	@Autowired
	private ISysRoleService iSysRoleService;

	@Autowired
	private ISysResourceService iSysResourceService;

	@Override
	public UserDto findByUserId(String userId) {
		SysUser sysUser = this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserId, userId));
		UserDto dto = BeanUtil.copyProperties(sysUser, UserDto.class);
		List<OrgChartDto> orgs = iSysOrgService.findListByUserId(userId);
		List<SysRole> roles = iSysRoleService.findListByUserId(userId);
		dto.setOrgIds(orgs.stream().map(OrgChartDto::getOrgId).collect(Collectors.joining(StrUtil.COMMA)));
		dto.setRoleIds(roles.stream().map(SysRole::getRoleId).collect(Collectors.joining(StrUtil.COMMA)));
		dto.setPassword(null);
		return dto;
	}

	@Override
	public User loadUserByUsername(String username) {
		SysUser sysUser = this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
		if (sysUser == null) {
			throw new UsernameNotFoundException("Not Found By Username.");
		}
		User user = BeanUtil.copyProperties(sysUser, User.class);
		List<SysRole> roles = iSysRoleService.findListByUserId(sysUser.getUserId());
		List<Long> sysRoleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
		List<ResourceDto> resources = iSysResourceService.findListBySysRoleIds(sysRoleIds, wrapper -> {

		});
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

	@Override
	public Page<SysUser> findPage(Page<SysUser> page, UserDto form) {
		return this.page(page, Wrappers.<SysUser>lambdaQuery().func(wrapper -> {
			wrapper.select(SysUser.class, field -> !StrUtil.equals(field.getColumn(), "password"));
			if (StrUtil.isNotBlank(form.getOrgIds())) {
				List<SysOrg> orgs = iSysOrgService.list(Wrappers.<SysOrg>lambdaQuery().in(SysOrg::getOrgId,
						StrUtil.splitTrim(form.getOrgIds(), StrUtil.COMMA)));
				if (CollUtil.isNotEmpty(orgs)) {
					List<Long> sysOrgIds = orgs.stream().map(SysOrg::getId).collect(Collectors.toList());
					sysOrgIds.forEach(it -> {
						CollUtil.addAll(orgs, iSysOrgService.findChilds(it));
					});
					if (CollUtil.isNotEmpty(sysOrgIds)) {
						sysOrgIds = orgs.stream().map(SysOrg::getId).collect(Collectors.toList());
						wrapper.inSql(SysUser::getId, StrUtil.indexedFormat(
								"select suo.sys_user_id from sys_user_org suo, sys_org so where suo.deleted = 0 and so.deleted = 0 and suo.sys_org_id = so.id and so.id in ({0})",
								StrUtil.join(StrUtil.COMMA, sysOrgIds)));
					}
				}
			}
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(it -> {
					it.or().like(SysUser::getUsername, form.getKeywords());
					it.or().like(SysUser::getNickname, form.getKeywords());
					it.or().like(SysUser::getMobile, form.getKeywords());
				});
			}
			wrapper.orderByDesc(SysUser::getCreatedAt);
		}));
	}
}
