package cn.renlm.mygraph.mxgraph.model;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.Data;

/**
 * 图形设计-Xml配置
 * 
 * @author RenLiMing(任黎明)
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
	private Double x;

	/**
	 * 坐标y
	 */
	@XStreamAsAttribute
	private Double y;

	/**
	 * 宽度
	 */
	@XStreamAsAttribute
	private Double width;

	/**
	 * 高度
	 */
	@XStreamAsAttribute
	private Double height;

	/**
	 * 类型标记
	 */
	@XStreamAsAttribute
	private String as = "geometry";

}