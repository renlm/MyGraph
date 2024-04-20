package cn.renlm.mygraph.modular.ds.dto;

import cn.renlm.mygraph.modular.ds.entity.Ds;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 数据源
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DsDto extends Ds {

	private static final long serialVersionUID = 1L;

	private String keywords;

}