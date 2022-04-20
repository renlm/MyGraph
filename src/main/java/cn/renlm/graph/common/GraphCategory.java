package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 图形分类
 * 
 * @author Renlm
 *
 */
public enum GraphCategory {

	UML("UML"), FLOW("流程图"), ER("ER模型"), GENERAL("一般设计"), OTHER("其它");

	@Getter
	private final String text;

	private GraphCategory(String text) {
		this.text = text;
	}
}