package cn.renlm.graph.modular.sys.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;
import cn.renlm.graph.modular.sys.mapper.SysRoleResourceMapper;
import cn.renlm.graph.modular.sys.service.ISysRoleResourceService;

/**
 * <p>
 * 角色资源关系 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class SysRoleResourceServiceImpl extends ServiceImpl<SysRoleResourceMapper, SysRoleResource>
		implements ISysRoleResourceService {

	@Override
	public List<SysRoleResource> findListByUser(String userId) {
		return this.list(Wrappers.<SysRoleResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRoleResource::getDeleted, false);
			wrapper.inSql(SysRoleResource::getId, StrUtil.indexedFormat(
					"select srr.id from sys_user su, sys_user_role sur, sys_role sr, sys_role_resource srr, sys_resource sr2 where su.user_id = ''{0}'' and su.id = sur.sys_user_id and sur.sys_role_id = sr.id and sr.id = srr.sys_role_id and srr.sys_resource_id = sr2.id and sur.deleted = 0 and sr.deleted = 0 and sr.disabled = 0 and srr.deleted = 0 and sr2.deleted = 0 and sr2.disabled = 0",
					userId));
			wrapper.orderByAsc(SysRoleResource::getId);
		}));
	}

	@Override
	public List<SysRoleResource> findListByRole(String roleId) {
		return this.list(Wrappers.<SysRoleResource>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRoleResource::getDeleted, false);
			wrapper.inSql(SysRoleResource::getId, StrUtil.indexedFormat(
					"select srr.id from sys_role sr, sys_role_resource srr, sys_resource sr2 where sr.role_id = ''{0}'' and sr.id = srr.sys_role_id and srr.sys_resource_id = sr2.id and srr.deleted = 0 and sr2.deleted = 0 and sr2.disabled = 0",
					roleId));
			wrapper.orderByAsc(SysRoleResource::getId);
		}));
	}
}
