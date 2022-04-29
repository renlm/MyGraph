package cn.renlm.graph.modular.sys.service.impl;

import java.util.ArrayList;
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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.modular.sys.dto.OrgChartDto;
import cn.renlm.graph.modular.sys.dto.OrgDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.entity.SysUserOrg;
import cn.renlm.graph.modular.sys.mapper.SysOrgMapper;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.modular.sys.service.ISysUserOrgService;
import cn.renlm.graph.modular.sys.service.ISysUserService;

/**
 * <p>
 * 组织机构 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2021-04-19
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements ISysOrgService {

	@Autowired
	private ISysUserService iSysUserService;

	@Autowired
	private ISysUserOrgService iSysUserOrgService;

	@Override
	public List<OrgChartDto> findListByUserId(String userId) {
		if (StrUtil.isBlank(userId)) {
			return CollUtil.newArrayList();
		}
		List<Long> sysOrgIds = CollUtil.newArrayList();
		Map<Long, SysUserOrg> map = new LinkedHashMap<>();
		SysUser sysUser = iSysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserId, userId));
		if (sysUser == null) {
			return CollUtil.newArrayList();
		}
		iSysUserOrgService.list(Wrappers.<SysUserOrg>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysUserOrg::getDeleted, false);
			wrapper.eq(SysUserOrg::getSysUserId, sysUser.getId());
		})).forEach(it -> {
			sysOrgIds.add(it.getSysOrgId());
			map.put(it.getSysOrgId(), it);
		});
		if (CollUtil.isEmpty(sysOrgIds)) {
			return CollUtil.newArrayList();
		}
		return this.list(Wrappers.<SysOrg>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysOrg::getDeleted, false);
			wrapper.in(SysOrg::getId, sysOrgIds);
			wrapper.orderByAsc(SysOrg::getSort);
			wrapper.orderByAsc(SysOrg::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			OrgChartDto dto = BeanUtil.copyProperties(obj, OrgChartDto.class);
			dto.setName(sysUser.getNickname());
			dto.setMobile(sysUser.getMobile());
			dto.setEmail(sysUser.getEmail());
			dto.setOrgId(obj.getOrgId());
			dto.setFathers(this.findFathers(dto.getId()));
			SysUserOrg rel = map.get(dto.getId());
			if (rel != null) {
				dto.setPositionCode(rel.getPositionCode());
				dto.setPositionName(rel.getPositionName());
			}
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<OrgChartDto> findTreeList() {
		List<OrgChartDto> orgCharts = new ArrayList<>();
		OrgChartDto root = new OrgChartDto();
		root.setId(-1L).setName("组织机构树").setState(TreeState.closed.name());
		List<OrgChartDto> list = this.list(Wrappers.<SysOrg>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysOrg::getDeleted, false);
			wrapper.orderByAsc(SysOrg::getSort);
			wrapper.orderByAsc(SysOrg::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			OrgChartDto dto = BeanUtil.copyProperties(obj, OrgChartDto.class);
			if (dto.getPid() == null) {
				dto.setPid(root.getId());
			}
			if (StrUtil.isNotBlank(obj.getLeaderUserId())) {
				List<OrgChartDto> rels = this.findListByUserId(obj.getLeaderUserId());
				rels = rels.stream().filter(it -> {
					dto.setName(it.getName());
					dto.setMobile(it.getMobile());
					dto.setEmail(it.getEmail());
					if (CollUtil.isEmpty(it.getFathers())) {
						return false;
					}
					return it.getFathers().stream().map(SysOrg::getId).collect(Collectors.toList())
							.contains(dto.getId());
				}).collect(Collectors.toList());
				dto.setPositionCode(rels.stream().map(OrgChartDto::getPositionCode).filter(it -> StrUtil.isNotBlank(it))
						.collect(Collectors.joining(StrUtil.COMMA)));
				dto.setPositionName(rels.stream().map(OrgChartDto::getPositionName).filter(it -> StrUtil.isNotBlank(it))
						.collect(Collectors.joining(StrUtil.COMMA)));
			}
			return dto;
		}).collect(Collectors.toList());
		orgCharts.add(root);
		orgCharts.addAll(list);
		return orgCharts;
	}

	@Override
	public List<OrgDto> findListByLevel(Integer level) {
		return this.list(Wrappers.<SysOrg>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysOrg::getDeleted, false);
			wrapper.eq(SysOrg::getLevel, level);
			wrapper.orderByAsc(SysOrg::getSort);
			wrapper.orderByAsc(SysOrg::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			OrgDto dto = BeanUtil.copyProperties(obj, OrgDto.class);
			long cnt = this.count(Wrappers.<SysOrg>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysOrg::getDeleted, false);
				wrapper.eq(SysOrg::getPid, obj.getId());
			}));
			dto.setIsLeaf(cnt == 0);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<OrgDto> findListByPid(Long pid) {
		return this.list(Wrappers.<SysOrg>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysOrg::getDeleted, false);
			wrapper.eq(SysOrg::getPid, pid);
			wrapper.orderByAsc(SysOrg::getSort);
			wrapper.orderByAsc(SysOrg::getCreatedAt);
		})).stream().filter(Objects::nonNull).map(obj -> {
			OrgDto dto = BeanUtil.copyProperties(obj, OrgDto.class);
			long cnt = this.count(Wrappers.<SysOrg>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysOrg::getDeleted, false);
				wrapper.eq(SysOrg::getPid, obj.getId());
			}));
			dto.setIsLeaf(cnt == 0);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<SysOrg> findFathers(Long id) {
		List<SysOrg> list = new ArrayList<>();
		SysOrg sysOrg = this.getById(id);
		while (sysOrg != null) {
			list.add(sysOrg);
			sysOrg = this.getById(sysOrg.getPid());
		}
		return list;
	}

	@Override
	public List<OrgDto> findChilds(Long pid) {
		List<OrgDto> list = CollUtil.newArrayList();
		list.add(BeanUtil.copyProperties(this.getById(pid), OrgDto.class));
		List<OrgDto> childs = this.findListByPid(pid);
		CollUtil.addAll(list, childs);
		for (OrgDto child : childs) {
			CollUtil.addAll(list, this.findChilds(child.getId()));
		}
		return list;
	}

	@Override
	public OrgDto findDetailByOrgId(String orgId) {
		SysOrg sysOrg = this.getOne(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getOrgId, orgId));
		OrgDto dto = BeanUtil.copyProperties(sysOrg, OrgDto.class);
		long cnt = this.count(Wrappers.<SysOrg>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysOrg::getDeleted, false);
			wrapper.eq(SysOrg::getPid, dto.getId());
		}));
		dto.setIsLeaf(cnt == 0);
		return dto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delCascadeByOrgId(String orgId) {
		int cnt = 0;
		SysOrg sysOrg = this.getOne(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getOrgId, orgId));
		if (sysOrg != null) {
			cnt++;
			sysOrg.setDeleted(true);
			sysOrg.setUpdatedAt(new Date());
			this.updateById(sysOrg);
			List<SysOrg> childs = this.list(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getPid, sysOrg.getId()));
			if (CollUtil.isNotEmpty(childs)) {
				for (SysOrg child : childs) {
					cnt += this.delCascadeByOrgId(child.getOrgId());
				}
			}
		}
		return cnt;
	}
}
