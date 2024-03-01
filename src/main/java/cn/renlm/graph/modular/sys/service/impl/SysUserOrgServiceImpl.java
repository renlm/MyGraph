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
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.entity.SysUserOrg;
import cn.renlm.graph.modular.sys.mapper.SysUserOrgMapper;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.modular.sys.service.ISysUserOrgService;
import cn.renlm.graph.modular.sys.service.ISysUserService;

/**
 * <p>
 * 用户组织机构关系 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
@Service
public class SysUserOrgServiceImpl extends ServiceImpl<SysUserOrgMapper, SysUserOrg> implements ISysUserOrgService {

	@Autowired
	private ISysUserService iSysUserService;

	@Autowired
	private ISysOrgService iSysOrgService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveRelationships(String userId, List<String> orgIds) {
		if (StrUtil.isBlank(userId)) {
			return;
		}

		// 是否清空用户组织机构关系
		SysUser sysUser = iSysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserId, userId));
		if (CollUtil.isEmpty(orgIds)) {
			this.update(Wrappers.<SysUserOrg>lambdaUpdate().func(wrapper -> {
				wrapper.set(SysUserOrg::getDeleted, true);
				wrapper.set(SysUserOrg::getUpdatedAt, new Date());
				wrapper.eq(SysUserOrg::getDeleted, false);
				wrapper.eq(SysUserOrg::getSysUserId, sysUser.getId());
			}));
			return;
		}

		// 获取原有关系
		List<SysOrg> orgs = iSysOrgService.list(Wrappers.<SysOrg>lambdaQuery().in(SysOrg::getOrgId, orgIds));
		List<SysUserOrg> rels = this.list(Wrappers.<SysUserOrg>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysUserOrg::getDeleted, false);
			wrapper.eq(SysUserOrg::getSysUserId, sysUser.getId());
		}));

		// 删除原有关系
		rels.forEach(it -> {
			it.setDeleted(true);
			it.setUpdatedAt(new Date());
		});

		// 保存新关系
		for (SysOrg org : orgs) {
			List<SysUserOrg> exists = rels.stream().filter(it -> NumberUtil.equals(it.getSysOrgId(), org.getId()))
					.collect(Collectors.toList());
			if (CollUtil.isNotEmpty(exists)) {
				exists.forEach(it -> {
					it.setDeleted(false);
					it.setUpdatedAt(new Date());
				});
			} else {
				SysUserOrg entity = new SysUserOrg();
				entity.setSysUserId(sysUser.getId());
				entity.setSysOrgId(org.getId());
				entity.setDeleted(false);
				entity.setCreatedAt(new Date());
				rels.add(entity);
			}
		}
		this.saveOrUpdateBatch(rels);
	}
}
