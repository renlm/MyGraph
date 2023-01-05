package cn.renlm.graph.service;

import static cn.hutool.core.text.StrPool.COMMA;

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
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.modular.sys.dto.SysResourceDto;
import cn.renlm.graph.modular.sys.dto.SysUserDto;
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
import cn.renlm.graph.util.TreeExtraUtil;

/**
 * 角色授权
 * 
 * @author Renlm
 *
 */
@Service
public class SysAuthAccessService {

	@Autowired
	private ISysUserService iSysUserService;

	@Autowired
	private ISysOrgService iSysOrgService;

	@Autowired
	private ISysRoleService iSysRoleService;

	@Autowired
	private ISysUserRoleService iSysUserRoleService;

	@Autowired
	private ISysResourceService iSysResourceService;

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	/**
	 * 获取角色授权资源列表
	 * 
	 * @param authAccessed
	 * @param roleId
	 * @param root
	 * @param pid
	 * @return
	 */
	public List<Tree<Long>> getTree(boolean authAccessed, String roleId, boolean root, Long pid) {
		if (StrUtil.isBlank(roleId)) {
			return CollUtil.newArrayList();
		}
		SysRole sysRole = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		if (ObjectUtil.isEmpty(sysRole)) {
			return CollUtil.newArrayList();
		}
		Map<Long, SysResourceDto> authAccessedMap = new LinkedHashMap<>();
		List<SysResourceDto> authAccessedList = iSysResourceService.findListByRole(roleId);
		authAccessedList.forEach(srd -> {
			authAccessedMap.put(srd.getId(), srd);
		});
		if (CollUtil.isEmpty(authAccessedList)) {
			if (TreeState.closed.name().equals(sysRole.getState())) {
				return CollUtil.newArrayList();
			}
		}
		if (BooleanUtil.isTrue(authAccessed)) {
			Tree<Long> top = new Tree<>();
			List<Tree<Long>> tree = TreeUtil.build(authAccessedList, pid, (object, treeNode) -> {
				BeanUtil.copyProperties(object, treeNode);
				treeNode.setId(object.getId());
				treeNode.setName(object.getText());
				treeNode.setWeight(object.getSort());
				treeNode.setParentId(object.getPid());
				treeNode.putExtra("roleId", sysRole.getRoleId());
				if (BooleanUtil.isFalse(root)) {
					return;
				}
				if (pid == null) {
					return;
				}
				if (NumberUtil.equals(pid, object.getId())) {
					BeanUtil.copyProperties(treeNode, top);
				}
			});
			if (ObjectUtil.isNotEmpty(top)) {
				top.setChildren(tree);
				return TreeExtraUtil.resetLevel(CollUtil.newArrayList(top), 1);
			} else {
				return TreeExtraUtil.resetLevel(tree, 1);
			}
		}
		List<Tree<Long>> tree = iSysResourceService.getTree(root, pid, false);
		TreeExtraUtil.foreach(tree, node -> {
			node.putExtra("roleId", sysRole.getRoleId());
			SysResourceDto srd = authAccessedMap.get(node.getId());
			if (ObjectUtil.isNotEmpty(srd)) {
				node.putExtra("accessAuth", true);
				node.putExtra("alias", srd.getAlias());
				node.putExtra("sort", srd.getSort());
				node.putExtra("defaultHomePage", srd.getDefaultHomePage());
			} else {
				node.putExtra("accessAuth", false);
			}
		});
		return tree;
	}

	/**
	 * 添加授权
	 * 
	 * @param roleId
	 * @param resourceIds
	 */
	@Transactional(rollbackFor = Exception.class)
	public void grant(String roleId, List<String> resourceIds) {
		if (StrUtil.isBlank(roleId) || CollUtil.isEmpty(resourceIds)) {
			return;
		}

		// 查询角色资源
		SysRole role = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		List<SysResourceDto> resources = iSysResourceService
				.list(Wrappers.<SysResource>lambdaQuery().in(SysResource::getResourceId, resourceIds)).stream()
				.filter(Objects::nonNull).map(obj -> {
					SysResourceDto dto = BeanUtil.copyProperties(obj, SysResourceDto.class);
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
		List<Long> ids = CollUtil.distinct(allSysResourceIds);
		List<SysResource> list = iSysResourceService.listByIds(ids);
		List<SysRoleResource> rels = CollUtil.newArrayList();
		for (SysResource sysResource : list) {
			SysRoleResource rel = new SysRoleResource();
			rel.setSysRoleId(sysRoleId);
			rel.setSysResourceId(sysResource.getId());
			rel.setSort(sysResource.getSort());
			rel.setDefaultHomePage(sysResource.getDefaultHomePage());
			rel.setCreatedAt(new Date());
			rels.add(rel);
		}
		if (CollUtil.isNotEmpty(rels)) {
			iSysRoleResourceService.saveBatch(rels);
		}
	}

	/**
	 * 取消授权
	 * 
	 * @param roleId
	 * @param resourceIds
	 */
	@Transactional(rollbackFor = Exception.class)
	public void unGrant(String roleId, List<String> resourceIds) {
		if (StrUtil.isBlank(roleId) || CollUtil.isEmpty(resourceIds)) {
			return;
		}

		// 查询角色资源
		SysRole role = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		List<SysResourceDto> resources = iSysResourceService
				.list(Wrappers.<SysResource>lambdaQuery().in(SysResource::getResourceId, resourceIds)).stream()
				.filter(Objects::nonNull).map(obj -> {
					SysResourceDto dto = BeanUtil.copyProperties(obj, SysResourceDto.class);
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
			List<Tree<Long>> list = TreeExtraUtil.getAllNodes(iSysResourceService.getTree(true, sysResourceId, true));
			CollUtil.addAll(allSysResourceIds, list.stream().map(Tree::getId).collect(Collectors.toList()));
		}

		// 删除原有关系
		Long sysRoleId = role.getId();
		List<Long> ids = CollUtil.distinct(allSysResourceIds);
		iSysRoleResourceService.update(Wrappers.<SysRoleResource>lambdaUpdate().func(wrapper -> {
			wrapper.eq(SysRoleResource::getSysRoleId, sysRoleId);
			wrapper.in(SysRoleResource::getSysResourceId, ids);
			wrapper.set(SysRoleResource::getDeleted, true);
			wrapper.set(SysRoleResource::getUpdatedAt, new Date());
		}));
	}

	/**
	 * 授权人员分页列表
	 * 
	 * @param page
	 * @param roleId
	 * @param form
	 * @return
	 */
	public Page<SysUserDto> getPageUsers(Page<SysUser> page, String roleId, SysUserDto form) {
		Page<SysUserDto> result = new Page<>(page.getCurrent(), page.getSize());
		BeanUtil.copyProperties(page, result);
		if (StrUtil.isBlank(roleId)) {
			return result;
		}
		SysRole sysRole = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		if (ObjectUtil.isEmpty(sysRole)) {
			return result;
		}
		Page<SysUser> pager = iSysUserService.page(page, Wrappers.<SysUser>lambdaQuery().func(wrapper -> {
			String sql = StrUtil.indexedFormat(
					"select sur.sys_user_id from sys_user_role sur, sys_role sr where sur.deleted = 0 and sr.deleted = 0 and sur.sys_role_id = sr.id and sr.id in ({0})",
					StrUtil.join(COMMA, sysRole.getId()));
			if (BooleanUtil.isTrue(form.getAccessAuth())) {
				wrapper.inSql(SysUser::getId, sql);
			} else if (BooleanUtil.isFalse(form.getAccessAuth())) {
				wrapper.notInSql(SysUser::getId, sql);
			}
			if (StrUtil.isNotBlank(form.getOrgIds())) {
				List<Long> allSysOrgIds = CollUtil.newArrayList();
				List<SysOrg> orgs = iSysOrgService.list(Wrappers.<SysOrg>lambdaQuery().in(SysOrg::getOrgId,
						StrUtil.splitTrim(form.getOrgIds(), COMMA)));
				if (CollUtil.isNotEmpty(orgs)) {
					List<Long> sysOrgIds = orgs.stream().map(SysOrg::getId).collect(Collectors.toList());
					sysOrgIds.forEach(it -> {
						List<Tree<Long>> list = TreeExtraUtil.getAllNodes(iSysOrgService.getTree(true, it));
						CollUtil.addAll(allSysOrgIds, list.stream().map(Tree::getId).collect(Collectors.toList()));
					});
					if (CollUtil.isNotEmpty(allSysOrgIds)) {
						wrapper.inSql(SysUser::getId, StrUtil.indexedFormat(
								"select suo.sys_user_id from sys_user_org suo, sys_org so where suo.deleted = 0 and so.deleted = 0 and suo.sys_org_id = so.id and so.id in ({0})",
								StrUtil.join(COMMA, allSysOrgIds)));
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
			wrapper.orderByDesc(SysUser::getId);
		}));
		return result.setRecords(pager.getRecords().stream().filter(Objects::nonNull).map(obj -> {
			SysUserDto data = BeanUtil.copyProperties(obj, SysUserDto.class);
			long cnt = iSysUserRoleService.count(Wrappers.<SysUserRole>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysUserRole::getDeleted, false);
				wrapper.eq(SysUserRole::getSysUserId, data.getId());
				wrapper.eq(SysUserRole::getSysRoleId, sysRole.getId());
			}));
			data.setAccessAuth(cnt > 0);
			return data;
		}).collect(Collectors.toList()));
	}

	/**
	 * 添加授权人员
	 * 
	 * @param roleId
	 * @param userIds
	 */
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
		if (CollUtil.isNotEmpty(rels)) {
			iSysUserRoleService.saveBatch(rels);
		}
	}

	/**
	 * 移除授权人员
	 * 
	 * @param roleId
	 * @param userIds
	 */
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
