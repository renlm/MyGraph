package cn.renlm.mygraph.modular.er.dto;

import java.util.List;

import cn.renlm.mygraph.modular.er.entity.Er;
import cn.renlm.mygraph.modular.er.entity.ErField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * ER模型
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ErDto extends Er {

	private static final long serialVersionUID = 1L;

	private Boolean includeFields;

	private String dsUuid;

	private String keywords;

	private List<ErField> fields;

}