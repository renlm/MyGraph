package cn.renlm.graph.util;

import java.util.List;
import java.util.function.Consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import lombok.experimental.UtilityClass;

/**
 * 树工具类（扩展）
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class TreeExtraUtil {

	/**
	 * 遍历树
	 * 
	 * @param <T>
	 * @param tree
	 * @param node
	 */
	public static final <T> void foreach(List<Tree<T>> tree, Consumer<Tree<T>> node) {
		if (CollUtil.isEmpty(tree)) {
			return;
		}
		for (Tree<T> item : tree) {
			node.accept(item);
			if (CollUtil.isNotEmpty(item.getChildren())) {
				foreach(item.getChildren(), node);
			}
		}
	}

	/**
	 * 重设层级
	 * 
	 * @param <T>
	 * @param tree
	 * @param level
	 * @return
	 */
	public static final <T> List<Tree<T>> resetLevel(List<Tree<T>> tree, int level) {
		if (CollUtil.isEmpty(tree)) {
			return CollUtil.newArrayList();
		}
		for (Tree<T> node : tree) {
			node.putExtra("level", level);
			if (CollUtil.isNotEmpty(node.getChildren())) {
				resetLevel(node.getChildren(), level + 1);
			}
		}
		return tree;
	}

	/**
	 * 获取全部节点
	 * 
	 * @param <T>
	 * @param tree
	 * @return
	 */
	public static final <T> List<Tree<T>> getAllNodes(List<Tree<T>> tree) {
		List<Tree<T>> nodes = CollUtil.newArrayList();
		if (CollUtil.isEmpty(tree)) {
			return nodes;
		}
		for (Tree<T> node : tree) {
			nodes.add(node);
			if (CollUtil.isNotEmpty(node.getChildren())) {
				nodes.addAll(getAllNodes(node.getChildren()));
			}
		}
		return nodes;
	}
}