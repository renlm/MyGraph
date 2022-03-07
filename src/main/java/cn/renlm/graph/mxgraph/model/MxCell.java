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
@XStreamAlias("mxCell")
public class MxCell implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 唯一标识
	 */
	@XStreamAsAttribute
	private String id;

	/**
	 * 内容
	 */
	@XStreamAsAttribute
	private String value;

	/**
	 * 样式
	 */
	@XStreamAsAttribute
	private String style;

	/**
	 * 父节点ID
	 */
	@XStreamAsAttribute
	private String parent;

	/**
	 * 节点
	 */
	@XStreamAsAttribute
	private Integer vertex;

	/**
	 * 几何形状
	 */
	private MxGeometry mxGeometry;

}