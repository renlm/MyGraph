package cn.renlm.graph.mxgraph.model;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Data;

/**
 * 图形设计-Xml配置
 * 
 * @author Renlm
 *
 */
@Data
@XStreamAlias("root")
public class Root implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 节点集
	 */
	@XStreamImplicit(itemFieldName = "mxCell")
	private List<MxCell> mxCells;

	/**
	 * 节点集
	 */
	@XStreamImplicit(itemFieldName = "UserObject")
	private List<UserObject> userObjects;

}