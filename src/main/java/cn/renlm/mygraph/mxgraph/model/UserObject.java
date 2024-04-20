package cn.renlm.mygraph.mxgraph.model;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Data;

/**
 * 图形设计-Xml配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@XStreamAlias("UserObject")
public class UserObject implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 唯一标识
	 */
	@XStreamAsAttribute
	private String id;

	/**
	 * 标签
	 */
	@XStreamAsAttribute
	private String label;

	/**
	 * 链接
	 */
	@XStreamAsAttribute
	private String link;

	/**
	 * 节点集
	 */
	@XStreamImplicit(itemFieldName = "mxCell")
	private List<MxCell> mxCells;

}