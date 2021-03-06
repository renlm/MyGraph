package cn.renlm.graph.modular.er.dto;

import java.util.List;

import cn.renlm.graph.modular.er.entity.Er;
import cn.renlm.graph.modular.er.entity.ErField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * ER模型
 * 
 * @author Renlm
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