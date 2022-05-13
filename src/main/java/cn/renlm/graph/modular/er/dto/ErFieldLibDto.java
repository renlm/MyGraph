package cn.renlm.graph.modular.er.dto;

import cn.renlm.graph.modular.er.entity.ErFieldLib;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * ER模型-我的字段库
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ErFieldLibDto extends ErFieldLib {

	private static final long serialVersionUID = 1L;

	private String keywords;

}