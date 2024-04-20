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
@XStreamAlias("mxGraphModel")
public class MxGraphModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 水平平移
	 */
	@XStreamAsAttribute
	private Double dx = (double) 2367;

	/**
	 * 垂直平移
	 */
	@XStreamAsAttribute
	private Double dy = (double) 1188;

	/**
	 * 网格
	 */
	@XStreamAsAttribute
	private Integer grid = 1;

	/**
	 * 网格大小（默认1）
	 */
	@XStreamAsAttribute
	private Double gridSize = (double) 1;

	/**
	 * 连接箭头
	 */
	@XStreamAsAttribute
	private Integer arrows = 0;

	/**
	 * 连接点
	 */
	@XStreamAsAttribute
	private Integer connect = 1;

	/**
	 * 参考线
	 */
	@XStreamAsAttribute
	private Integer guides = 1;

	/**
	 * 页面视图
	 */
	@XStreamAsAttribute
	private Integer page = 0;

	/**
	 * 背景
	 */
	@XStreamAsAttribute
	private String background;

	/**
	 * 根
	 */
	private Root root;

}