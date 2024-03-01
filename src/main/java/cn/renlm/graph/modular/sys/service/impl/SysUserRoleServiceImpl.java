package cn.renlm.graph.modular.sys.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.entity.SysUserRole;
import cn.renlm.graph.modular.sys.mapper.SysUserRoleMapper;
import cn.renlm.graph.modular.sys.service.ISysRoleService;
import cn.renlm.graph.modular.sys.service.ISysUserRoleService;
import cn.renlm.graph.modular.sys.service.ISysUserService;

/**
 * <p>
 * 用户角色关系 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

	@Autowired
	private ISysUserService iSysUserService;

	@Autowired
	private ISysRoleService iSysRoleService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveRelationships(String userId, List<String> roleIds) {
		if (StrUtil.isBlank(userId)) {
			return;
		}

		// 是否清空用户角色关系
		SysUser sysUser = iSysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserId, userId));
		if (CollUtil.isEmpty(roleIds)) {
			this.update(Wrappers.<SysUserRole>lambdaUpdate().func(wrapper -> {
				wrapper.set(SysUserRole::getDeleted, true);
				wrapper.set(SysUserRole::getUpdatedAt, new Date());
				wrapper.eq(SysUserRole::getDeleted, false);
				wrapper.eq(SysUserRole::getSysUserId, sysUser.getId());
			}));
			return;
		}

		// 获取原有关系
		List<SysRole> roles = iSysRoleService.list(Wrappers.<SysRole>lambdaQuery().in(SysRole::getRoleId, roleIds));
		List<SysUserRole> rels = this.list(Wrappers.<SysUserRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysUserRole::getDeleted, false);
			wrapper.eq(SysUserRole::getSysUserId, sysUser.getId());
		}));

		// 删除原有关系
		rels.forEach(it -> {
			it.setDeleted(true);
			it.setUpdatedAt(new Date());
		});

		// 保存新关系
		for (SysRole role : roles) {
			List<SysUserRole> exists = rels.stream()
					.filter(it -> it.getSysRoleId() != null && NumberUtil.equals(it.getSysRoleId(), role.getId()))
					.collect(Collectors.toList());
			if (CollUtil.isNotEmpty(exists)) {
				exists.forEach(it -> {
					it.setDeleted(false);
					it.setUpdatedAt(new Date());
				});
			} else {
				SysUserRole entity = new SysUserRole();
				entity.setSysUserId(sysUser.getId());
				entity.setSysRoleId(role.getId());
				entity.setDeleted(false);
				entity.setCreatedAt(new Date());
				rels.add(entity);
			}
		}
		this.saveOrUpdateBatch(rels);
	}
}
