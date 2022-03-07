package cn.renlm.graph.mxgraph.model;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.Data;

/**
 * 图形设计-Xml配置
 * 
 * @author Renlm
 *
 */
@Data
@XStreamAlias("mxGeometry")
public class MxGeometry implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 坐标x
	 */
	@XStreamAsAttribute
	private Integer x;

	/**
	 * 坐标y
	 */
	@XStreamAsAttribute
	private Integer y;

	/**
	 * 宽度
	 */
	@XStreamAsAttribute
	private Integer width;

	/**
	 * 高度
	 */
	@XStreamAsAttribute
	private Integer height;

	/**
	 * 类型标记
	 */
	@XStreamAsAttribute
	private String as = "geometry";

}