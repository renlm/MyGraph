package cn.renlm.graph.modular.sys.service.impl;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.renlm.graph.modular.sys.dto.SysOrgDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.mapper.SysOrgMapper;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.modular.sys.service.ISysUserService;

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
		SysOrg sysDict = this.getById(id);
		List<SysOrg> list = CollUtil.newArrayList(sysDict);
		int level = sysDict.getLevel();
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
		});
		if (pid == null || BooleanUtil.isFalse(root)) {
			if (CollUtil.isEmpty(tree)) {
				return CollUtil.newArrayList();
			} else {
				return tree;
			}
		}
		Tree<Long> top = new Tree<>();
		SysOrg parent = this.getById(pid);
		BeanUtil.copyProperties(parent, top);
		top.setId(parent.getId());
		top.setName(parent.getText());
		top.setWeight(parent.getSort());
		top.setParentId(parent.getPid());
		top.setChildren(tree);
		return CollUtil.newArrayList(top);
	}
}
