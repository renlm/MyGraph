package cn.renlm.graph.modular.sys.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.Resource;
import cn.renlm.graph.modular.sys.dto.ResourceDto;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.mapper.SysResourceMapper;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleService;

/**
 * <p>
 * 资源 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
@Service
public class SysResourceServiceImpl extends ServiceImpl<SysResourceMapper, SysResource> implements ISysResourceService {

	@Autowired
	private ISysRoleService iSysRoleService;

	@Autowired
	private ISysRoleResourceService iSysRoleResourceService;

	@Override
	public List<SysResource> findHomePagesByUserId(String userId) {
		List<SysRole> roles = iSysRoleService.findListByUserId(userId);
		SysResource welcome = this
				.getOne(Wrappers.<SysResource>lambdaQuery().eq(SysResource::getCode, Resource.WELCOME));
		welcome.setUrl(StrUtil.removePrefix(welcome.getUrl(), StrUtil.SLASH));
		List<Long> sysRoleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
		if (CollUtil.isEmpty(sysRoleIds)) {
			return CollUtil.newArrayList(welcome);
		}
		List<ResourceDto> resources = this.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			wrapper.eq(SysResource::getDisabled, false);
			wrapper.inSql(SysResource::getId, StrUtil.indexedFormat(
					"select sr.id from sys_resource sr, sys_role_resource srr where sr.deleted = 0 and srr.deleted = 0 and sr.id = srr.sys_resource_id and srr.sys_role_id in ({0})",
					StrUtil.join(StrUtil.COMMA, sysRoleIds)));
			wrapper.orderByAsc(SysResource::getLevel);
			wrapper.orderByAsc(SysResource::getSort);
			wrapper.orderByAsc(SysResource::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			return BeanUtil.copyProperties(obj, ResourceDto.class);
		}).collect(Collectors.toList());
		this.dealCustomBySysRoleResource(sysRoleIds, resources);
		resources = resources.stream().filter(it -> BooleanUtil.isTrue(it.getDefaultHomePage()))
				.collect(Collectors.toList());
		if (CollUtil.isEmpty(resources)) {
			return CollUtil.newArrayList(welcome);
		}
		resources.forEach(it -> {
			if (!StrUtil.startWithAny(it.getUrl(), "http", "//")) {
				it.setUrl(StrUtil.removePrefix(it.getUrl(), StrUtil.SLASH));
			}
		});
		return Convert.toList(SysResource.class, resources);
	}

	@Override
	public List<ResourceDto> findChildsByUserId(String userId, Long pid, Resource.Type... types) {
		List<SysRole> roles = iSysRoleService.findListByUserId(userId);
		List<Long> sysRoleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
		if (CollUtil.isEmpty(sysRoleIds)) {
			return CollUtil.newArrayList();
		}
		List<String> resourceTypeCodes = CollUtil.newArrayList();
		for (Resource.Type type : types) {
			resourceTypeCodes.add(type.name());
		}
		List<ResourceDto> resources = this.findListBySysRoleIds(sysRoleIds, wrapper -> {
			if (pid == null) {
				wrapper.isNull(SysResource::getPid);
			} else {
				wrapper.eq(SysResource::getPid, pid);
			}
			if (CollUtil.isNotEmpty(resourceTypeCodes)) {
				wrapper.in(SysResource::getResourceTypeCode, resourceTypeCodes);
			}
		});
		this.dealCustomBySysRoleResource(sysRoleIds, resources);
		return resources;
	}

	@Override
	public List<ResourceDto> findCommonMenusByUserId(String userId, String text) {
		List<SysRole> roles = iSysRoleService.findListByUserId(userId);
		List<Long> sysRoleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
		if (CollUtil.isEmpty(sysRoleIds)) {
			return CollUtil.newArrayList();
		}
		List<ResourceDto> resources = this.findListBySysRoleIds(sysRoleIds, wrapper -> {
			wrapper.eq(SysResource::getCommonly, true);
			if (StrUtil.isNotBlank(text)) {
				wrapper.like(SysResource::getText, text);
			}
		});
		this.dealCustomBySysRoleResource(sysRoleIds, resources);
		return resources;
	}

	/**
	 * 处理角色资源关系自定义配置
	 * 
	 * @param sysRoleIds
	 * @param resources
	 */
	private void dealCustomBySysRoleResource(List<Long> sysRoleIds, List<ResourceDto> resources) {
		// 自定义配置（尽量在单角色分配时使用，多个角色可能被覆盖）
		Map<Long, SysRoleResource> map = new LinkedHashMap<>();
		List<SysRoleResource> rels = iSysRoleResourceService
				.list(Wrappers.<SysRoleResource>lambdaQuery().func(wrapper -> {
					wrapper.eq(SysRoleResource::getDeleted, false);
					wrapper.in(SysRoleResource::getSysRoleId, sysRoleIds);
					wrapper.orderByAsc(SysRoleResource::getSort);
					wrapper.orderByAsc(SysRoleResource::getCreatedAt);
				}));
		rels.forEach(it -> {
			map.put(it.getSysResourceId(), it);
		});
		for (int i = 0; i < resources.size(); i++) {
			ResourceDto dto = resources.get(i);
			SysRoleResource rel = map.get(dto.getId());
			if (rel != null) {
				dto.setSysRoleResourceId(rel.getId());
				dto.setText(ObjectUtil.defaultIfBlank(rel.getAlias(), dto.getText()));
				dto.setSort(ObjectUtil.defaultIfNull(rel.getSort(), dto.getSort()));
				dto.setCommonly(ObjectUtil.defaultIfNull(rel.getCommonly(), dto.getCommonly()));
				dto.setDefaultHomePage(ObjectUtil.defaultIfNull(rel.getDefaultHomePage(), dto.getDefaultHomePage()));
				dto.setHide(ObjectUtil.defaultIfNull(rel.getHide(), false));
				if (BooleanUtil.isTrue(rel.getHide())) {
					resources.remove(i--);
				}
			}
		}
		// 重新排序
		CollUtil.sort(resources, (o1, o2) -> {
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
	}

	@Override
	public List<ResourceDto> findListBySysRoleIds(List<Long> sysRoleIds,
			Consumer<LambdaQueryWrapper<SysResource>> wrapper) {
		if (CollUtil.isEmpty(sysRoleIds)) {
			return CollUtil.newArrayList();
		}
		return this.list(Wrappers.<SysResource>lambdaQuery().func(wrappers -> {
			wrappers.eq(SysResource::getDeleted, false);
			wrappers.eq(SysResource::getDisabled, false);
			wrappers.inSql(SysResource::getId, StrUtil.indexedFormat(
					"select sr.id from sys_resource sr, sys_role_resource srr where sr.deleted = 0 and srr.deleted = 0 and sr.id = srr.sys_resource_id and srr.sys_role_id in ({0})",
					StrUtil.join(StrUtil.COMMA, sysRoleIds)));
			wrappers.orderByAsc(SysResource::getSort);
			wrappers.orderByAsc(SysResource::getCreatedAt);
			wrapper.accept(wrappers);
		})).stream().filter(Objects::nonNull).map(obj -> {
			ResourceDto dto = BeanUtil.copyProperties(obj, ResourceDto.class);
			long cnt = this.count(Wrappers.<SysResource>lambdaQuery().func(wrappers -> {
				wrappers.eq(SysResource::getDeleted, false);
				wrappers.eq(SysResource::getDisabled, false);
				wrappers.eq(SysResource::getPid, obj.getId());
			}));
			dto.setIsLeaf(cnt == 0);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<ResourceDto> findListByLevel(Integer level) {
		return this.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			wrapper.eq(SysResource::getLevel, level);
			wrapper.orderByAsc(SysResource::getSort);
			wrapper.orderByAsc(SysResource::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			ResourceDto dto = BeanUtil.copyProperties(obj, ResourceDto.class);
			long cnt = this.count(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysResource::getDeleted, false);
				wrapper.eq(SysResource::getPid, obj.getId());
			}));
			dto.setIsLeaf(cnt == 0);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<ResourceDto> findListByPid(Long pid) {
		return this.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			wrapper.eq(SysResource::getPid, pid);
			wrapper.orderByAsc(SysResource::getSort);
			wrapper.orderByAsc(SysResource::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			ResourceDto dto = BeanUtil.copyProperties(obj, ResourceDto.class);
			long cnt = this.count(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysResource::getDeleted, false);
				wrapper.eq(SysResource::getPid, obj.getId());
			}));
			dto.setIsLeaf(cnt == 0);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<SysResource> findFathers(Long id) {
		List<SysResource> list = new ArrayList<>();
		SysResource sysResource = this.getById(id);
		while (sysResource != null) {
			list.add(sysResource);
			sysResource = this.getById(sysResource.getPid());
		}
		return list;
	}

	@Override
	public List<ResourceDto> findChilds(Long pid) {
		List<ResourceDto> list = CollUtil.newArrayList();
		list.add(BeanUtil.copyProperties(this.getById(pid), ResourceDto.class));
		List<ResourceDto> childs = this.findListByPid(pid);
		CollUtil.addAll(list, childs);
		for (ResourceDto child : childs) {
			CollUtil.addAll(list, this.findChilds(child.getId()));
		}
		return list;
	}

	@Override
	public ResourceDto findDetailByResourceId(String resourceId) {
		SysResource sysResource = this
				.getOne(Wrappers.<SysResource>lambdaQuery().eq(SysResource::getResourceId, resourceId));
		ResourceDto dto = BeanUtil.copyProperties(sysResource, ResourceDto.class);
		long cnt = this.count(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysResource::getDeleted, false);
			wrapper.eq(SysResource::getPid, dto.getId());
		}));
		dto.setIsLeaf(cnt == 0);
		return dto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delCascadeByResourceId(String resourceId) {
		int cnt = 0;
		SysResource sysResource = this
				.getOne(Wrappers.<SysResource>lambdaQuery().eq(SysResource::getResourceId, resourceId));
		if (sysResource != null) {
			cnt++;
			sysResource.setDeleted(true);
			sysResource.setUpdatedAt(new Date());
			this.updateById(sysResource);
			List<SysResource> childs = this
					.list(Wrappers.<SysResource>lambdaQuery().eq(SysResource::getPid, sysResource.getId()));
			if (CollUtil.isNotEmpty(childs)) {
				for (SysResource child : childs) {
					cnt += this.delCascadeByResourceId(child.getResourceId());
				}
			}
		}
		return cnt;
	}
}
