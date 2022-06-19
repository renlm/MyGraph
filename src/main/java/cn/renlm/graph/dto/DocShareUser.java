package cn.renlm.graph.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 认证用户（文档分享）
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
public class DocShareUser implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 分享Uuid
	 */
	private String shareUuid;

}