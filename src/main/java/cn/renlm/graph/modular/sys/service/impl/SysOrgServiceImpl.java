package cn.renlm.graph.modular.sys.service.impl;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import cn.hutool.extra.spring.SpringUtil;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.modular.sys.dto.SysOrgDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.mapper.SysOrgMapper;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.modular.sys.service.ISysUserService;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.TreeExtraUtil;

/**
 * <p>
 * 组织机构 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements ISysOrgService {

	@Override
	public List<SysOrgDto> findListByUser(String userId) {
		return this.baseMapper.findListByUser(userId);
	}

	@Override
	public List<SysOrg> findListByPid(Long pid) {
		return this.list(Wrappers.<SysOrg>lambdaQuery().func(wrapper -> {
			if (pid == null) {
				wrapper.isNull(SysOrg::getPid);
				wrapper.eq(SysOrg::getLevel, 1);
			} else {
				wrapper.eq(SysOrg::getPid, pid);
			}
			wrapper.orderByAsc(SysOrg::getSort);
			wrapper.orderByAsc(SysOrg::getId);
		}));
	}

	@Override
	public List<SysOrg> findFathers(Long id) {
		if (id == null) {
			return CollUtil.newArrayList();
		}
		SysOrg sysOrg = this.getById(id);
		List<SysOrg> list = CollUtil.newArrayList(sysOrg);
		int level = sysOrg.getLevel();
		while (--level > 0) {
			list.add(this.getById(CollUtil.getLast(list).getPid()));
		}
		return CollUtil.reverse(list);
	}

	@Override
	public List<Tree<Long>> getTree(boolean root, Long pid) {
		List<SysOrg> list = this.list();
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
			if (StrUtil.isNotBlank(object.getLeaderUserId())) {
				SysUser sysUser = SpringUtil.getBean(ISysUserService.class)
						.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserId, object.getLeaderUserId()));
				treeNode.putExtra("leaderUserName", sysUser.getNickname());
				treeNode.putExtra("mobile", sysUser.getMobile());
				treeNode.putExtra("email", sysUser.getEmail());
				List<SysOrgDto> orgs = this.findListByUser(sysUser.getUserId());
				orgs.forEach(org -> {
					List<SysOrg> fathers = this.findFathers(org.getId());
					List<Long> fatherIds = fathers.stream().map(SysOrg::getId).collect(Collectors.toList());
					if (fatherIds.contains(object.getId())) {
						Object n = treeNode.get("positionName");
						Object on = org.getPositionName();
						treeNode.putExtra("positionName", n == null ? on : StrUtil.join(COMMA, n, on));
					}
				});
			}
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
	@Transactional
	public Result<SysOrg> ajaxSave(SysOrg sysOrg) {
		SysOrg exists = this.getOne(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getCode, sysOrg.getCode()));
		if (StrUtil.isBlank(sysOrg.getOrgId())) {
			// 校验组织机构代码（是否存在）
			if (exists != null) {
				return Result.error("组织机构代码重复");
			}
			sysOrg.setOrgId(IdUtil.getSnowflakeNextIdStr());
			sysOrg.setCreatedAt(new Date());
		} else {
			SysOrg entity = this.getOne(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getOrgId, sysOrg.getOrgId()));
			// 校验组织机构代码（是否存在）
			if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
				return Result.error("组织机构代码重复");
			}
			sysOrg.setId(entity.getId());
			sysOrg.setCreatedAt(entity.getCreatedAt());
			sysOrg.setUpdatedAt(new Date());
			sysOrg.setDeleted(entity.getDeleted());
		}
		if (sysOrg.getPid() == null) {
			sysOrg.setLevel(1);
		} else {
			SysOrg parent = this.getById(sysOrg.getPid());
			parent.setState(TreeState.closed.name());
			sysOrg.setLevel(parent.getLevel() + 1);
			List<SysOrg> fathers = this.findFathers(parent.getId());
			List<Long> fatherIds = fathers.stream().map(SysOrg::getId).collect(Collectors.toList());
			if (fatherIds.contains(sysOrg.getId())) {
				return Result.error("不能选择自身或子节点作为父级");
			} else {
				parent.setUpdatedAt(new Date());
				this.updateById(parent);
			}
		}
		this.saveOrUpdate(sysOrg);
		return Result.success(sysOrg);
	}
}
