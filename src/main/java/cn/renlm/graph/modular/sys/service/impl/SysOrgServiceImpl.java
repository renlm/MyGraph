package cn.renlm.graph.modular.sys.service.impl;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.dto.SysOrgDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.mapper.SysOrgMapper;
import cn.renlm.graph.modular.sys.service.ISysOrgService;

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
			if (object.getLeaderUserId() != null) {
				List<SysOrgDto> orgs = this.findListByUser(object.getLeaderUserId());
				orgs.forEach(org -> {
					Object p = treeNode.get("positionCode");
					Object n = treeNode.get("positionName");
					Object op = org.getPositionCode();
					Object on = org.getPositionName();
					treeNode.put("leaderUserName", org.getLeaderUserName());
					treeNode.put("positionCode", p == null ? op : StrUtil.join(COMMA, p, op));
					treeNode.put("positionName", n == null ? on : StrUtil.join(COMMA, n, on));
					treeNode.put("mobile", org.getMobile());
					treeNode.put("email", org.getEmail());
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
