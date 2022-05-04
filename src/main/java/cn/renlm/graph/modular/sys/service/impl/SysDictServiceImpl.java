package cn.renlm.graph.modular.sys.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.mapper.SysDictMapper;
import cn.renlm.graph.modular.sys.service.ISysDictService;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

	@Override
	public List<Tree<Long>> getTree(String... codePaths) {
		Long pid = null;
		if (codePaths.length > 0) {
			List<SysDict> nodes = this.findListByPath(codePaths);
			if (CollUtil.isEmpty(nodes)) {
				return CollUtil.newArrayList();
			} else {
				pid = CollUtil.getLast(nodes).getId();
			}
		}
		List<SysDict> list = this.list();
		return TreeUtil.build(list, pid, (object, treeNode) -> {
			BeanUtil.copyProperties(object, treeNode);
			treeNode.setId(object.getId());
			treeNode.setName(object.getText());
			treeNode.setWeight(object.getSort());
			treeNode.setParentId(object.getPid());
		});
	}

	@Override
	public List<SysDict> findListByPath(String... codePaths) {
		List<SysDict> list = CollUtil.newArrayList();
		for (String code : codePaths) {
			SysDict node = this.getOne(Wrappers.<SysDict>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysDict::getCode, code);
				if (CollUtil.isEmpty(list)) {
					wrapper.eq(SysDict::getLevel, 1);
				} else {
					wrapper.eq(SysDict::getPid, CollUtil.getLast(list).getId());
				}
			}));
			if (node == null) {
				return CollUtil.newArrayList();
			} else {
				list.add(node);
			}
		}
		return list;
	}

	@Override
	public List<SysDict> findListByPUuid(String pUuid) {
		SysDict parent = new SysDict();
		if (StrUtil.isNotBlank(pUuid)) {
			SysDict sysDict = this.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getUuid, pUuid));
			if (sysDict == null) {
				return CollUtil.newArrayList();
			} else {
				BeanUtil.copyProperties(sysDict, parent);
			}
		}
		return this.list(Wrappers.<SysDict>lambdaQuery().func(wrapper -> {
			if (StrUtil.isBlank(pUuid)) {
				wrapper.isNull(SysDict::getPid);
				wrapper.eq(SysDict::getLevel, 1);
			} else {
				wrapper.eq(SysDict::getPid, parent.getId());
			}
			wrapper.orderByAsc(SysDict::getSort);
			wrapper.orderByAsc(SysDict::getId);
		}));
	}
}
