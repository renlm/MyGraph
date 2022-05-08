package cn.renlm.graph.modular.sys.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.ConstVal;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.dto.SysOrgDto;
import cn.renlm.graph.modular.sys.dto.SysUserDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.mapper.SysUserMapper;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleService;
import cn.renlm.graph.modular.sys.service.ISysUserOrgService;
import cn.renlm.graph.modular.sys.service.ISysUserRoleService;
import cn.renlm.graph.modular.sys.service.ISysUserService;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.TreeExtraUtil;

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
	private ISysUserRoleService iSysUserRoleService;

	@Autowired
	private ISysResourceService iSysResourceService;

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	@Autowired
	private ISysOrgService iSysOrgService;

	@Autowired
	private ISysUserOrgService iSysUserOrgService;

	@Override
	public Page<SysUser> findPage(Page<SysUser> page, SysUserDto form) {
		Page<SysUser> data = this.page(page, Wrappers.<SysUser>lambdaQuery().func(wrapper -> {
			if (StrUtil.isNotBlank(form.getOrgIds())) {
				List<String> orgIds = StrUtil.splitTrim(form.getOrgIds(), StrUtil.COMMA);
				List<SysOrg> sysOrgs = iSysOrgService.list(Wrappers.<SysOrg>lambdaQuery().in(SysOrg::getOrgId, orgIds));
				List<Tree<Long>> orgTrees = CollUtil.newArrayList();
				sysOrgs.forEach(sysOrg -> {
					iSysOrgService.getTree(true, sysOrg.getId());
				});
				List<Tree<Long>> orgNodes = TreeExtraUtil.getAllNodes(orgTrees);
				wrapper.inSql(SysUser::getId, StrUtil.indexedFormat(
						"select suo.sys_user_id from sys_org so, sys_user_org suo where so.id in ({0}) and so.id = suo.sys_org_id and so.deleted = 0 and suo.deleted = 0",
						orgNodes.stream().map(it -> String.valueOf(it.getId()))
								.collect(Collectors.joining(StrUtil.COMMA))));
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
		data.getRecords().forEach(item -> {
			item.setPassword(null);
		});
		return data;
	}

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
		List<SysOrgDto> orgs = iSysOrgService.findListByUser(sysUser.getUserId());

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
		user.setOrgs(orgs);
		return user.setAuthorities(authorities);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<SysUserDto> ajaxSave(SysUserDto form) {
		// 校验账号（格式）
		if (!ReUtil.isMatch(ConstVal.username_reg, form.getUsername())) {
			return Result.error(ConstVal.username_msg);
		}
		SysUser exists = this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, form.getUsername()));
		if (form.getUserId() == null) {
			// 校验账号（是否存在）
			if (exists != null) {
				return Result.error("登录账号已存在");
			}
			// 校验密码（格式）
			if (!ReUtil.isMatch(ConstVal.password_reg, form.getPassword())) {
				return Result.error(ConstVal.password_msg);
			}
			form.setUserId(IdUtil.simpleUUID().toUpperCase());
			form.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
			form.setCreatedAt(new Date());
		} else {
			SysUser entity = this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserId, form.getUserId()));
			// 校验账号（是否存在）
			if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
				return Result.error("登录账号已存在");
			}
			form.setId(entity.getId());
			form.setPassword(entity.getPassword());
			form.setAccountNonExpired(entity.getAccountNonExpired());
			form.setCredentialsNonExpired(entity.getCredentialsNonExpired());
			form.setCreatedAt(entity.getCreatedAt());
			form.setUpdatedAt(new Date());
		}
		this.saveOrUpdate(form);
		iSysUserOrgService.saveRelationships(form.getUserId(), StrUtil.splitTrim(form.getOrgIds(), StrUtil.COMMA));
		iSysUserRoleService.saveRelationships(form.getUserId(), StrUtil.splitTrim(form.getRoleIds(), StrUtil.COMMA));
		return Result.success(form);
	}
}
