package cn.renlm.mygraph.modular.er.dto;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
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

	public List<ErField> getPkList() {
		List<ErField> list = new ArrayList<>();
		if (CollUtil.isNotEmpty(this.getFields())) {
			this.getFields().forEach(it -> {
				if (BooleanUtil.isTrue(it.getIsFk())) {
					list.add(it);
				}
			});
		}
		{
			return list;
		}
	}

}
