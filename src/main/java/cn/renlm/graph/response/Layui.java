package cn.renlm.graph.response;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Layui
 *
 * @author Renlm
 */
@Data
@Accessors(chain = true)
public class Layui<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 响应码
	 */
	private int code = 0;

	/**
	 * 消息
	 */
	private String msg;

	/**
	 * 数据
	 */
	private T data;

	/**
	 * 响应成功
	 * 
	 * @param <R>
	 * @param data
	 * @return
	 */
	public static <R> Layui<R> success(R data) {
		Layui<R> layui = new Layui<>();
		layui.setData(data);
		return layui;
	}
}