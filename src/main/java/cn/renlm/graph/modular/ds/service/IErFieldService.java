package cn.renlm.graph.modular.ds.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.ds.entity.ErField;

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
	 * @param erUuid
	 * @return
	 */
	List<ErField> findListByErUuid(String erUuid);

}
