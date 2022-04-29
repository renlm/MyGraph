package cn.renlm.graph.modular.sys.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.dto.RoleDto;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.mapper.SysRoleMapper;
import cn.renlm.graph.modular.sys.service.ISysRoleService;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2020-01-01
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

	@Override
	public List<SysRole> findListByUserId(String userId) {
		return this.list(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRole::getDeleted, false);
			wrapper.inSql(SysRole::getId, StrUtil.indexedFormat(
					"select sur.sys_role_id from sys_user su, sys_user_role sur where sur.deleted = 0 and su.id = sur.sys_user_id and su.user_id = ''{0}''",
					userId));
			wrapper.orderByAsc(SysRole::getSort);
			wrapper.orderByAsc(SysRole::getCreatedAt);
		}));
	}

	@Override
	public List<SysRole> findTreeList() {
		return this.list(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRole::getDeleted, false);
			wrapper.orderByAsc(SysRole::getLevel);
			wrapper.orderByAsc(SysRole::getSort);
			wrapper.orderByAsc(SysRole::getCreatedAt);
		}));
	}

	@Override
	public List<RoleDto> findListByLevel(Integer level) {
		return this.list(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRole::getDeleted, false);
			wrapper.eq(SysRole::getLevel, level);
			wrapper.orderByAsc(SysRole::getSort);
			wrapper.orderByAsc(SysRole::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			RoleDto dto = BeanUtil.copyProperties(obj, RoleDto.class);
			long cnt = this.count(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysRole::getDeleted, false);
				wrapper.eq(SysRole::getPid, obj.getId());
			}));
			dto.setIsLeaf(cnt == 0);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<RoleDto> findListByPid(Long pid) {
		return this.list(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRole::getDeleted, false);
			wrapper.eq(SysRole::getPid, pid);
			wrapper.orderByAsc(SysRole::getSort);
			wrapper.orderByAsc(SysRole::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			RoleDto dto = BeanUtil.copyProperties(obj, RoleDto.class);
			long cnt = this.count(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysRole::getDeleted, false);
				wrapper.eq(SysRole::getPid, obj.getId());
			}));
			dto.setIsLeaf(cnt == 0);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<SysRole> findFathers(Long id) {
		List<SysRole> list = new ArrayList<>();
		SysRole sysRole = this.getById(id);
		while (sysRole != null) {
			list.add(sysRole);
			sysRole = this.getById(sysRole.getPid());
		}
		return list;
	}

	@Override
	public List<RoleDto> findChilds(Long pid) {
		List<RoleDto> list = CollUtil.newArrayList();
		list.add(BeanUtil.copyProperties(this.getById(pid), RoleDto.class));
		List<RoleDto> childs = this.findListByPid(pid);
		CollUtil.addAll(list, childs);
		for (RoleDto child : childs) {
			CollUtil.addAll(list, this.findChilds(child.getId()));
		}
		return list;
	}

	@Override
	public RoleDto findDetailByRoleId(String roleId) {
		SysRole sysRole = this.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, roleId));
		RoleDto dto = BeanUtil.copyProperties(sysRole, RoleDto.class);
		long cnt = this.count(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRole::getDeleted, false);
			wrapper.eq(SysRole::getPid, dto.getId());
		}));
		dto.setIsLeaf(cnt == 0);
		return dto;
	}
}
