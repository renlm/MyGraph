package cn.renlm.graph.common;

import cn.renlm.plugins.Common.StrToEnum;

/**
 * 图形分类
 * 
 * @author Renlm
 *
 */
public enum GraphCategory implements StrToEnum.StrValue {

	UML("UML"), FLOW("流程图"), ER("ER模型"), GENERAL("一般设计"), OTHER("其它");

	private final String text;

	private GraphCategory(String text) {
		this.text = text;
	}

	@Override
	public String value() {
		return text;
	}
}