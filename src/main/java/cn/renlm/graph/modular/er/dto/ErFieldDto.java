package cn.renlm.graph.modular.er.dto;

import cn.renlm.graph.modular.er.entity.ErField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * ER模型-字段
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ErFieldDto extends ErField {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否被收藏到我的字段库
	 */
	private Boolean libing;

}