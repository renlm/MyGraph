package cn.renlm.graph.common;

import lombok.Getter;

/**
 * 资源
 * 
 * @author Renlm
 *
 */
public enum Resource {

	WELCOME("欢迎页"), 
	OSHI("服务器监控");

	@Getter
	private final String text;

	private Resource(String text) {
		this.text = text;
	}

	/**
	 * 类型
	 */
	public enum Type {

		menu("菜单"), 
		button("按钮"), 
		urlNewWindows("链接-新窗口"), 
		urlInsidePage("链接-内页"), 
		more("更多功能"), 
		permission("权限"),
		markdown("Markdown文档");

		@Getter
		private final String text;

		private Type(String text) {
			this.text = text;
		}
	}

}
