package cn.renlm.graph.modular.er.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldDto;
import cn.renlm.graph.modular.er.entity.ErField;

/**
 * <p>
 * ER模型-字段 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
public interface IErFieldService extends IService<ErField> {

	/**
	 * 获取ER模型字段列表
	 * 
	 * @param user
	 * @param erUuid
	 * @return
	 */
	List<ErFieldDto> findListByEr(User user, String erUuid);

}
