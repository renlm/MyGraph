package cn.renlm.graph.modular.sys.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.modular.sys.mapper.SysResourceMapper;
import cn.renlm.graph.modular.sys.service.ISysResourceService;

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
	public List<SysResource> findListByPid(Long pid) {
		return this.list(Wrappers.<SysResource>lambdaQuery().func(wrapper -> {
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
		SysResource sysDict = this.getById(id);
		List<SysResource> list = CollUtil.newArrayList(sysDict);
		int level = sysDict.getLevel();
		while (--level > 0) {
			list.add(this.getById(CollUtil.getLast(list).getPid()));
		}
		return CollUtil.reverse(list);
	}
}
