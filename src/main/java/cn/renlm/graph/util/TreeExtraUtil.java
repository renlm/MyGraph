package cn.renlm.graph.util;

import java.util.List;

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
}