package cn.renlm.mygraph.common;

import lombok.Getter;

/**
 * 图形分类
 * 
 * @author RenLiMing(任黎明)
 *
 */
public enum Mxgraph {

	UML("UML"), 
	BUS("业务设计"), 
	FLOWCHART("流程图"), 
	PLAN("平面图"), 
	NETWORK("网络架构"), 
	CLOUD("云架构"), 
	ER("ER模型"), 
	GENERAL("一般设计"),
	OTHER("其它");

	@Getter
	private final String text;

	private Mxgraph(String text) {
		this.text = text;
	}

}
