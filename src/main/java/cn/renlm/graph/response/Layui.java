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

	private int code = 0;

	private String msg;

	private T data;

}