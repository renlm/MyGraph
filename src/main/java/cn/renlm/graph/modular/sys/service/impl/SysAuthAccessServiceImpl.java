package cn.renlm.graph.modular.sys.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.modular.sys.dto.ResourceDto;
import cn.renlm.graph.modular.sys.dto.RoleDto;
import cn.renlm.graph.modular.sys.dto.UserDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.entity.SysUserRole;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleService;
import cn.renlm.graph.modular.sys.service.ISysUserRoleService;
import cn.renlm.graph.modular.sys.service.ISysUserService;
import cn.renlm.graph.modular.sys.service.SysAuthAccessService;

/**
 * 角色授权
 * 
 * @author Renlm
 *
 */
@Service
public class SysAuthAccessServiceImpl implements SysAuthAccessService {

	@Autowired
	private ISysUserService iSysUserService;

	@Autowired
	private ISysOrgService iSysOrgService;

	@Autowired
	private ISysRoleService iSysRoleService;

	@Autowired
	private ISysResourceService iSysResourceService;

	@Autowired
	private ISysUserRoleService iSysUserRoleService;

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	@Override
	public List<ResourceDto> getAllResourceByRoleId(String roleId) {
		if (StrUtil.isBlank(roleId)) {
			return CollUtil.newArrayList();
		}
		RoleDto role = iSysRoleService.findDetailByRoleId(roleId);
		if (!BooleanUtil.isTrue(role.getIsLeaf()) || StrUtil.equals(role.getState(), TreeState.closed.name())) {
			return CollUtil.newArrayList();
		}
		Map<Long, ResourceDto> map = new LinkedHashMap<>();
		List<ResourceDto> accessAuths = iSysResourceService.findListBySysRoleIds(CollUtil.newArrayList(role.getId()),
				wrapper -> {

				});
		accessAuths.forEach(it -> {
			map.put(it.getId(), it);
		});
		List<ResourceDto> list = iSysResourceService.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			wrapper.orderByAsc(SysResource::getSort);
			wrapper.orderByAsc(SysResource::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			ResourceDto dto = BeanUtil.copyProperties(obj, ResourceDto.class);
			dto.setAccessAuth(map.containsKey(dto.getId()));
			return dto;
		}).collect(Collectors.toList());
		return list;
	}

	@Override
	public List<ResourceDto> getAuthAccessResourceByRoleId(String roleId) {
		if (StrUtil.isBlank(roleId)) {
			return CollUtil.newArrayList();
		}
		RoleDto role = iSysRoleService.findDetailByRoleId(roleId);
		if (!BooleanUtil.isTrue(role.getIsLeaf()) || StrUtil.equals(role.getState(), TreeState.closed.name())) {
			return CollUtil.newArrayList();
		}
		Map<Long, ResourceDto> map = new LinkedHashMap<>();
		List<ResourceDto> accessAuths = iSysResourceService.findListBySysRoleIds(CollUtil.newArrayList(role.getId()),
				wrapper -> {

				});
		accessAuths.forEach(it -> {
			it.setAccessAuth(true);
			map.put(it.getId(), it);
		});
		List<SysRoleResource> rels = iSysRoleResourceService
				.list(Wrappers.<SysRoleResource>lambdaQuery().func(wrapper -> {
					wrapper.eq(SysRoleResource::getDeleted, false);
					wrapper.eq(SysRoleResource::getSysRoleId, role.getId());
				}));
		rels.forEach(it -> {
			ResourceDto dto = map.get(it.getSysResourceId());
			if (dto != null) {
				dto.setSysRoleResourceId(it.getId());
				dto.setAlias(it.getAlias());
				dto.setSort(ObjectUtil.defaultIfNull(it.getSort(), dto.getSort()));
				dto.setCommonly(ObjectUtil.defaultIfNull(it.getCommonly(), dto.getCommonly()));
				dto.setDefaultHomePage(ObjectUtil.defaultIfNull(it.getDefaultHomePage(), dto.getDefaultHomePage()));
				dto.setHide(ObjectUtil.defaultIfNull(it.getHide(), false));
			}
		});
		CollUtil.sort(accessAuths, (o1, o2) -> {
			if (o1.getLevel() < o2.getLevel()) {
				return -1;
			} else if (o1.getLevel() > o2.getLevel()) {
				return 1;
			} else {
				if (o1.getSort() < o2.getSort()) {
					return -1;
				} else if (o1.getSort() > o2.getSort()) {
					return 1;
				} else {
					return DateUtil.compare(o1.getCreatedAt(), o2.getCreatedAt());
				}
			}
		});
		return accessAuths;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<ResourceDto> grant(String roleId, List<String> resourceIds) {
		if (StrUtil.isBlank(roleId) || CollUtil.isEmpty(resourceIds)) {
			return CollUtil.newArrayList();
		}

		// 查询角色资源
		SysRole role = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		List<ResourceDto> resources = iSysResourceService
				.list(Wrappers.<SysResource>lambdaQuery().in(SysResource::getResourceId, resourceIds)).stream()
				.filter(Objects::nonNull).map(obj -> {
					ResourceDto dto = BeanUtil.copyProperties(obj, ResourceDto.class);
					dto.setAccessAuth(true);
					return dto;
				}).collect(Collectors.toList());

		// 删除原有关系
		Long sysRoleId = role.getId();
		List<Long> sysResourceIds = resources.stream().map(SysResource::getId).collect(Collectors.toList());
		iSysRoleResourceService.update(Wrappers.<SysRoleResource>lambdaUpdate().func(wrapper -> {
			wrapper.eq(SysRoleResource::getSysRoleId, sysRoleId);
			wrapper.in(SysRoleResource::getSysResourceId, sysResourceIds);
			wrapper.set(SysRoleResource::getDeleted, true);
			wrapper.set(SysRoleResource::getUpdatedAt, new Date());
		}));

		// 获取全路径资源节点集
		List<Long> allSysResourceIds = CollUtil.newArrayList();
		for (Long sysResourceId : sysResourceIds) {
			if (allSysResourceIds.contains(sysResourceId)) {
				continue;
			}
			List<SysResource> list = iSysResourceService.findFathers(sysResourceId);
			CollUtil.addAll(allSysResourceIds, list.stream().map(SysResource::getId).collect(Collectors.toList()));
		}

		// 批量保存
		List<Long> news = CollUtil.distinct(allSysResourceIds);
		Map<Long, SysResource> map = new LinkedHashMap<>();
		List<SysResource> list = iSysResourceService.listByIds(news);
		list.forEach(it -> {
			map.put(it.getId(), it);
		});
		List<SysRoleResource> rels = CollUtil.newArrayList();
		for (Long sysResourceId : news) {
			SysResource sysResource = map.get(sysResourceId);
			SysRoleResource rel = new SysRoleResource();
			rel.setSysRoleId(sysRoleId);
			rel.setSysResourceId(sysResourceId);
			rel.setSort(sysResource.getSort());
			rel.setCommonly(sysResource.getCommonly());
			rel.setDefaultHomePage(sysResource.getDefaultHomePage());
			rel.setHide(false);
			rel.setCreatedAt(new Date());
			rels.add(rel);
		}
		iSysRoleResourceService.saveBatch(rels);
		return this.getAllResourceByRoleId(roleId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<ResourceDto> ungrant(String roleId, List<String> resourceIds) {
		if (StrUtil.isBlank(roleId) || CollUtil.isEmpty(resourceIds)) {
			return CollUtil.newArrayList();
		}

		// 查询角色资源
		SysRole role = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		List<ResourceDto> resources = iSysResourceService
				.list(Wrappers.<SysResource>lambdaQuery().in(SysResource::getResourceId, resourceIds)).stream()
				.filter(Objects::nonNull).map(obj -> {
					ResourceDto dto = BeanUtil.copyProperties(obj, ResourceDto.class);
					dto.setAccessAuth(false);
					return dto;
				}).collect(Collectors.toList());

		// 获取全部子节点
		List<Long> allSysResourceIds = CollUtil.newArrayList();
		List<Long> sysResourceIds = resources.stream().map(SysResource::getId).collect(Collectors.toList());
		for (Long sysResourceId : sysResourceIds) {
			if (allSysResourceIds.contains(sysResourceId)) {
				continue;
			}
			List<ResourceDto> list = iSysResourceService.findChilds(sysResourceId);
			CollUtil.addAll(allSysResourceIds, list.stream().map(SysResource::getId).collect(Collectors.toList()));
		}

		// 删除原有关系
		List<Long> news = CollUtil.distinct(allSysResourceIds);
		iSysRoleResourceService.update(Wrappers.<SysRoleResource>lambdaUpdate().func(wrapper -> {
			wrapper.eq(SysRoleResource::getSysRoleId, role.getId());
			wrapper.in(SysRoleResource::getSysResourceId, news);
			wrapper.set(SysRoleResource::getDeleted, true);
			wrapper.set(SysRoleResource::getUpdatedAt, new Date());
		}));
		return this.getAllResourceByRoleId(roleId);
	}

	@Override
	public Page<UserDto> getAllUserByRoleIdAndPage(Page<SysUser> page, String roleId, UserDto form) {
		Page<UserDto> result = new Page<>(page.getCurrent(), page.getSize());
		BeanUtil.copyProperties(page, result, "records");
		if (StrUtil.isBlank(roleId)) {
			return result;
		}
		RoleDto role = iSysRoleService.findDetailByRoleId(roleId);
		if (!BooleanUtil.isTrue(role.getIsLeaf()) || StrUtil.equals(role.getState(), TreeState.closed.name())) {
			return result;
		}
		page = iSysUserService.page(page, Wrappers.<SysUser>lambdaQuery().func(wrapper -> {
			if (role != null) {
				String sql = StrUtil.indexedFormat(
						"select sur.sys_user_id from sys_user_role sur, sys_role sr where sur.deleted = 0 and sr.deleted = 0 and sur.sys_role_id = sr.id and sr.id in ({0})",
						StrUtil.join(StrUtil.COMMA, role.getId()));
				if (BooleanUtil.isTrue(form.getAccessAuth())) {
					wrapper.inSql(SysUser::getId, sql);
				} else if (BooleanUtil.isFalse(form.getAccessAuth())) {
					wrapper.notInSql(SysUser::getId, sql);
				}
			}
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
		BeanUtil.copyProperties(page, result, "records");
		return result.setRecords(page.getRecords().stream().filter(Objects::nonNull).map(obj -> {
			UserDto dto = BeanUtil.copyProperties(obj, UserDto.class);
			dto.setParamRoleId(roleId);
			long cnt = iSysUserRoleService.count(Wrappers.<SysUserRole>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysUserRole::getDeleted, false);
				wrapper.eq(SysUserRole::getSysUserId, dto.getId());
				wrapper.eq(SysUserRole::getSysRoleId, role.getId());
			}));
			dto.setAccessAuth(cnt > 0);
			return dto;
		}).collect(Collectors.toList()));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRoleMember(String roleId, List<String> userIds) {
		if (StrUtil.isBlank(roleId) || CollUtil.isEmpty(userIds)) {
			return;
		}

		// 查询角色用户
		SysRole role = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		List<SysUser> users = iSysUserService.list(Wrappers.<SysUser>lambdaQuery().in(SysUser::getUserId, userIds));

		// 删除原有关系
		List<Long> sysUserIds = users.stream().map(SysUser::getId).collect(Collectors.toList());
		iSysUserRoleService.update(Wrappers.<SysUserRole>lambdaUpdate().func(wrapper -> {
			wrapper.eq(SysUserRole::getSysRoleId, role.getId());
			wrapper.in(SysUserRole::getSysUserId, sysUserIds);
			wrapper.set(SysUserRole::getDeleted, true);
			wrapper.set(SysUserRole::getUpdatedAt, new Date());
		}));

		// 批量保存
		List<SysUserRole> rels = CollUtil.newArrayList();
		for (Long sysUserId : sysUserIds) {
			SysUserRole rel = new SysUserRole();
			rel.setSysUserId(sysUserId);
			rel.setSysRoleId(role.getId());
			rel.setCreatedAt(new Date());
			rel.setDeleted(false);
			rels.add(rel);
		}
		iSysUserRoleService.saveBatch(rels);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeRoleMember(String roleId, List<String> userIds) {
		if (StrUtil.isBlank(roleId) || CollUtil.isEmpty(userIds)) {
			return;
		}

		// 查询角色用户
		SysRole role = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		List<SysUser> users = iSysUserService.list(Wrappers.<SysUser>lambdaQuery().in(SysUser::getUserId, userIds));

		// 删除原有关系
		List<Long> sysUserIds = users.stream().map(SysUser::getId).collect(Collectors.toList());
		iSysUserRoleService.update(Wrappers.<SysUserRole>lambdaUpdate().func(wrapper -> {
			wrapper.eq(SysUserRole::getSysRoleId, role.getId());
			wrapper.in(SysUserRole::getSysUserId, sysUserIds);
			wrapper.set(SysUserRole::getDeleted, true);
			wrapper.set(SysUserRole::getUpdatedAt, new Date());
		}));
	}
}