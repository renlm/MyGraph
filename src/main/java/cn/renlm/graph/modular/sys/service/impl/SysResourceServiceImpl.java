package cn.renlm.graph.modular.sys.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.modular.sys.dto.SysResourceDto;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.mapper.SysResourceMapper;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.security.RequestAuthorizationManager;
import cn.renlm.graph.util.TreeExtraUtil;

/**
 * <p>
 * 资源 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class SysResourceServiceImpl extends ServiceImpl<SysResourceMapper, SysResource> implements ISysResourceService {

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	@Override
	public List<SysResource> findListByUser(String userId) {
		return this.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			wrapper.eq(SysResource::getDisabled, false);
			wrapper.inSql(SysResource::getId, StrUtil.indexedFormat(
					"select srr.sys_resource_id from sys_user su, sys_user_role sur, sys_role sr, sys_role_resource srr where su.user_id = ''{0}'' and su.id = sur.sys_user_id and sur.sys_role_id = sr.id and sr.id = srr.sys_role_id and sur.deleted = 0 and sr.deleted = 0 and sr.disabled = 0 and srr.deleted = 0",
					userId));
			wrapper.orderByAsc(SysResource::getLevel);
			wrapper.orderByAsc(SysResource::getSort);
			wrapper.orderByAsc(SysResource::getId);
		}));
	}

	@Override
	public List<SysResourceDto> findListByRole(String roleId) {
		Map<Long, SysResourceDto> srMap = new LinkedHashMap<>();
		List<SysRoleResource> roleResources = iSysRoleResourceService.findListByRole(roleId);
		List<SysResourceDto> resources = this.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			wrapper.eq(SysResource::getDisabled, false);
			wrapper.inSql(SysResource::getId, StrUtil.indexedFormat(
					"select srr.sys_resource_id from sys_role sr, sys_role_resource srr where sr.role_id = ''{0}'' and sr.id = srr.sys_role_id and srr.deleted = 0",
					roleId));
			wrapper.orderByAsc(SysResource::getLevel);
			wrapper.orderByAsc(SysResource::getSort);
			wrapper.orderByAsc(SysResource::getId);
		})).stream().filter(Objects::nonNull).map(obj -> {
			SysResourceDto data = BeanUtil.copyProperties(obj, SysResourceDto.class);
			data.setAccessAuth(true);
			srMap.put(data.getId(), data);
			return data;
		}).collect(Collectors.toList());
		if (CollUtil.isNotEmpty(roleResources)) {
			for (SysRoleResource srr : roleResources) {
				SysResourceDto sr = srMap.get(srr.getSysResourceId());
				if (sr != null) {
					sr.setAlias(srr.getAlias());
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
		return resources;
	}

	@Override
	public List<SysResource> findListByPid(Long pid) {
		return this.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			if (pid == null) {
				wrapper.isNull(SysResource::getPid);
				wrapper.eq(SysResource::getLevel, 1);
			} else {
				wrapper.eq(SysResource::getPid, pid);
			}
			wrapper.orderByAsc(SysResource::getSort);
			wrapper.orderByAsc(SysResource::getId);
		}));
	}

	@Override
	public List<SysResource> findFathers(Long id) {
		if (id == null) {
			return CollUtil.newArrayList();
		}
		SysResource sysResource = this.getById(id);
		List<SysResource> list = CollUtil.newArrayList(sysResource);
		int level = sysResource.getLevel();
		while (--level > 0) {
			list.add(this.getById(CollUtil.getLast(list).getPid()));
		}
		return CollUtil.reverse(list);
	}

	@Override
	public List<Tree<Long>> getTree(boolean root, Long pid, Boolean includeDisabled) {
		List<SysResource> list = this.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			if (BooleanUtil.isFalse(includeDisabled)) {
				wrapper.eq(SysResource::getDisabled, false);
			}
		}));
		if (CollUtil.isEmpty(list)) {
			return CollUtil.newArrayList();
		}
		Tree<Long> top = new Tree<>();
		List<Tree<Long>> tree = TreeUtil.build(list, pid, (object, treeNode) -> {
			BeanUtil.copyProperties(object, treeNode);
			treeNode.setId(object.getId());
			treeNode.setName(object.getText());
			treeNode.setWeight(object.getSort());
			treeNode.setParentId(object.getPid());
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<SysResource> ajaxSave(SysResource sysResource) {
		SysResource exists = this
				.getOne(Wrappers.<SysResource>lambdaQuery().eq(SysResource::getCode, sysResource.getCode()));
		if (StrUtil.isBlank(sysResource.getResourceId())) {
			// 校验资源编码（是否存在）
			if (exists != null) {
				return Result.error("资源编码重复");
			}
			sysResource.setResourceId(IdUtil.simpleUUID().toUpperCase());
			sysResource.setCreatedAt(new Date());
		} else {
			SysResource entity = this.getOne(
					Wrappers.<SysResource>lambdaQuery().eq(SysResource::getResourceId, sysResource.getResourceId()));
			// 校验资源编码（是否存在）
			if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
				return Result.error("资源编码重复");
			}
			sysResource.setId(entity.getId());
			sysResource.setCreatedAt(entity.getCreatedAt());
			sysResource.setUpdatedAt(new Date());
			sysResource.setDeleted(entity.getDeleted());
		}
		if (sysResource.getPid() == null) {
			sysResource.setLevel(1);
		} else {
			SysResource parent = this.getById(sysResource.getPid());
			parent.setState(TreeState.closed.name());
			sysResource.setLevel(parent.getLevel() + 1);
			List<SysResource> fathers = this.findFathers(parent.getId());
			List<Long> fatherIds = fathers.stream().map(SysResource::getId).collect(Collectors.toList());
			if (fatherIds.contains(sysResource.getId())) {
				return Result.error("不能选择自身或子节点作为父级资源");
			} else {
				parent.setUpdatedAt(new Date());
				this.updateById(parent);
			}
		}
		// 排序
		if (sysResource.getSort() == null) {
			List<SysResource> childs = this.findListByPid(sysResource.getPid());
			OptionalInt max = childs.stream().filter(Objects::nonNull).mapToInt(SysResource::getSort).max();
			if (max.isPresent()) {
				sysResource.setSort(max.getAsInt() + 1);
			}
		}
		sysResource.setSort(ObjectUtil.defaultIfNull(sysResource.getSort(), 1));
		this.saveOrUpdate(sysResource);
		// 子节点更新
		Map<String, Integer> map = new LinkedHashMap<>();
		List<Tree<Long>> roots = this.getTree(true, null, true);
		TreeExtraUtil.resetLevel(roots, 1);
		roots.forEach(root -> {
			Tree<Long> node = TreeUtil.getNode(root, sysResource.getId());
			if (ObjectUtil.isNotEmpty(node)) {
				List<Tree<Long>> childs = TreeExtraUtil.getAllNodes(CollUtil.newArrayList(node));
				childs.forEach(child -> {
					SysResource sr = BeanUtil.copyProperties(child, SysResource.class);
					map.put(sr.getResourceId(), sr.getLevel());
				});
			}
		});
		map.forEach((resourceId, level) -> {
			this.update(Wrappers.<SysResource>lambdaUpdate().func(wrapper -> {
				wrapper.set(SysResource::getLevel, level);
				wrapper.set(SysResource::getUpdatedAt, new Date());
				wrapper.eq(SysResource::getResourceId, resourceId);
			}));
		});
		RequestAuthorizationManager.REQUEST_MATCHER_MAP.clear();
		return Result.success(sysResource);
	}

}
