package cn.renlm.graph.service;

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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.modular.sys.dto.SysResourceDto;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleService;
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
	private ISysRoleService iSysRoleService;

	@Autowired
	private ISysResourceService iSysResourceService;

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	/**
	 * 获取角色授权资源列表
	 * 
	 * @param roleId
	 * @param root
	 * @param pid
	 * @return
	 */
	public List<Tree<Long>> getTree(String roleId, boolean root, Long pid) {
		if (StrUtil.isBlank(roleId)) {
			return CollUtil.newArrayList();
		}
		SysRole sysRole = iSysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		if (ObjectUtil.isEmpty(sysRole)) {
			return CollUtil.newArrayList();
		}
		Map<Long, SysResourceDto> authAccessedMap = new LinkedHashMap<>();
		List<SysResourceDto> authAccessed = iSysResourceService.findListByRole(roleId);
		authAccessed.forEach(srd -> {
			authAccessedMap.put(srd.getId(), srd);
		});
		if (CollUtil.isEmpty(authAccessed)) {
			if (TreeState.closed.name().equals(sysRole.getState())) {
				return CollUtil.newArrayList();
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
}