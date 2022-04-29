package cn.renlm.graph.modular.sys.dto;

import cn.renlm.graph.modular.sys.entity.SysDict;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDto extends SysDict {

	private static final long serialVersionUID = 1L;

	private Boolean isLeaf;

}