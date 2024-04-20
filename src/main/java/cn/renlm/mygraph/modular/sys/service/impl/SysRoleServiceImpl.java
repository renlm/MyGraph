package cn.renlm.mygraph.modular.sys.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
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
import cn.renlm.mygraph.common.TreeState;
import cn.renlm.mygraph.modular.sys.entity.SysRole;
import cn.renlm.mygraph.modular.sys.mapper.SysRoleMapper;
import cn.renlm.mygraph.modular.sys.service.ISysRoleService;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.plugins.MyUtil.MyTreeExtraUtil;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

	@Override
	public List<SysRole> findListByUser(String userId) {
		return this.list(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRole::getDeleted, false);
			wrapper.eq(SysRole::getDisabled, false);
			wrapper.inSql(SysRole::getId, StrUtil.indexedFormat(
					"select sur.sys_role_id from sys_user su, sys_user_role sur where su.user_id = ''{0}'' and su.id = sur.sys_user_id and sur.deleted = 0",
					userId));
			wrapper.orderByAsc(SysRole::getLevel);
			wrapper.orderByAsc(SysRole::getSort);
			wrapper.orderByAsc(SysRole::getId);
		}));
	}

	@Override
	public List<SysRole> findListByPid(Long pid) {
		return this.list(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRole::getDeleted, false);
			if (pid == null) {
				wrapper.isNull(SysRole::getPid);
				wrapper.eq(SysRole::getLevel, 1);
			} else {
				wrapper.eq(SysRole::getPid, pid);
			}
			wrapper.orderByAsc(SysRole::getSort);
			wrapper.orderByAsc(SysRole::getId);
		}));
	}

	@Override
	public List<SysRole> findFathers(Long id) {
		if (id == null) {
			return CollUtil.newArrayList();
		}
		SysRole sysRole = this.getById(id);
		List<SysRole> list = CollUtil.newArrayList(sysRole);
		int level = sysRole.getLevel();
		while (--level > 0) {
			list.add(this.getById(CollUtil.getLast(list).getPid()));
		}
		return CollUtil.reverse(list);
	}

	@Override
	public List<Tree<Long>> getTree(boolean root, Long pid) {
		List<SysRole> list = this.list(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getDeleted, false));
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
			return MyTreeExtraUtil.resetLevel(CollUtil.newArrayList(top), 1);
		} else {
			return MyTreeExtraUtil.resetLevel(tree, 1);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<SysRole> ajaxSave(SysRole sysRole) {
		SysRole exists = this.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getCode, sysRole.getCode()));
		if (StrUtil.isBlank(sysRole.getRoleId())) {
			// 校验角色代码（是否存在）
			if (exists != null) {
				return Result.error("角色代码重复");
			}
			sysRole.setRoleId(IdUtil.simpleUUID().toUpperCase());
			sysRole.setCreatedAt(new Date());
		} else {
			SysRole entity = this.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleId, sysRole.getRoleId()));
			// 校验角色代码（是否存在）
			if (exists != null && !NumberUtil.equals(exists.getId(), entity.getId())) {
				return Result.error("角色代码重复");
			}
			sysRole.setId(entity.getId());
			sysRole.setCreatedAt(entity.getCreatedAt());
			sysRole.setUpdatedAt(new Date());
			sysRole.setDeleted(entity.getDeleted());
		}
		if (sysRole.getPid() == null) {
			sysRole.setLevel(1);
		} else {
			SysRole parent = this.getById(sysRole.getPid());
			parent.setState(TreeState.closed.name());
			sysRole.setLevel(parent.getLevel() + 1);
			List<SysRole> fathers = this.findFathers(parent.getId());
			List<Long> fatherIds = fathers.stream().map(SysRole::getId).collect(Collectors.toList());
			if (fatherIds.contains(sysRole.getId())) {
				return Result.error("不能选择自身或子节点作为父级角色");
			} else {
				parent.setUpdatedAt(new Date());
				this.updateById(parent);
			}
		}
		// 排序
		if (sysRole.getSort() == null) {
			List<SysRole> childs = this.findListByPid(sysRole.getPid());
			OptionalInt max = childs.stream().filter(Objects::nonNull).mapToInt(SysRole::getSort).max();
			if (max.isPresent()) {
				sysRole.setSort(max.getAsInt() + 1);
			}
		}
		sysRole.setSort(ObjectUtil.defaultIfNull(sysRole.getSort(), 1));
		this.saveOrUpdate(sysRole);
		// 子节点更新
		Map<String, Integer> map = new LinkedHashMap<>();
		List<Tree<Long>> roots = this.getTree(true, null);
		MyTreeExtraUtil.resetLevel(roots, 1);
		roots.forEach(root -> {
			Tree<Long> node = TreeUtil.getNode(root, sysRole.getId());
			if (ObjectUtil.isNotEmpty(node)) {
				List<Tree<Long>> childs = MyTreeExtraUtil.getAllNodes(CollUtil.newArrayList(node));
				childs.forEach(child -> {
					SysRole sr = BeanUtil.copyProperties(child, SysRole.class);
					map.put(sr.getRoleId(), sr.getLevel());
				});
			}
		});
		map.forEach((roleId, level) -> {
			this.update(Wrappers.<SysRole>lambdaUpdate().func(wrapper -> {
				wrapper.set(SysRole::getLevel, level);
				wrapper.set(SysRole::getUpdatedAt, new Date());
				wrapper.eq(SysRole::getRoleId, roleId);
			}));
		});
		return Result.success(sysRole);
	}
}
