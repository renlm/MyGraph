package cn.renlm.graph.dto;

import cn.renlm.graph.entity.Ds;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 数据源
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DsDto extends Ds {

	private static final long serialVersionUID = 1L;

	private String keywords;

}