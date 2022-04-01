package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 启动环境
 * 
 * @author Renlm
 *
 */
public enum Profiles {

	dev("开发环境"), test("测试环境"), prod("正式环境");

	@Getter
	private String text;

	private Profiles(String text) {
		this.text = text;
	}
}