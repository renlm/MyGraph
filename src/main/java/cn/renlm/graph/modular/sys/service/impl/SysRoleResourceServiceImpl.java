package cn.renlm.graph.modular.sys.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.dto.RoleResourceDto;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.mapper.SysRoleResourceMapper;
import cn.renlm.graph.modular.sys.service.ISysResourceService;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;

/**
 * <p>
 * 角色资源关系 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
@Service
public class SysRoleResourceServiceImpl extends ServiceImpl<SysRoleResourceMapper, SysRoleResource>
		implements ISysRoleResourceService {

	@Autowired
	private ISysResourceService iSysResourceService;

	@Override
	public RoleResourceDto findDetailById(Long id) {
		SysRoleResource sysRoleResource = this.getById(id);
		RoleResourceDto dto = BeanUtil.copyProperties(sysRoleResource, RoleResourceDto.class);
		List<SysResource> fathers = iSysResourceService.findFathers(dto.getSysResourceId());
		SysResource sysResource = CollUtil.getFirst(fathers);
		dto.setResourceCode(sysResource.getCode());
		dto.setResourceTypeCode(sysResource.getResourceTypeCode());
		dto.setResourceNames(CollUtil.reverse(fathers).stream().map(SysResource::getText)
				.collect(Collectors.joining(StrUtil.SLASH)));
		dto.setSort(ObjectUtil.defaultIfNull(dto.getSort(), sysResource.getSort()));
		dto.setCommonly(ObjectUtil.defaultIfNull(dto.getCommonly(), sysResource.getCommonly()));
		dto.setDefaultHomePage(ObjectUtil.defaultIfNull(dto.getDefaultHomePage(), sysResource.getDefaultHomePage()));
		dto.setHide(ObjectUtil.defaultIfNull(dto.getHide(), false));
		return dto;
	}
}
