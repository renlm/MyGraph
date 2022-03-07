package cn.renlm.graph.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.entity.ErField;

/**
 * <p>
 * ER模型-字段 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-03-04
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
