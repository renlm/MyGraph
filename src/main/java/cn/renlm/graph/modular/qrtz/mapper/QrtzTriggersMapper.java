package cn.renlm.graph.modular.qrtz.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.modular.qrtz.dto.QrtzTriggersDto;

/**
 * 触发器
 * 
 * @author Renlm
 *
 */
public interface QrtzTriggersMapper {

	/**
	 * 任务名称是否存在
	 * 
	 * @param jobName
	 * @return
	 */
	boolean exists(String jobName);

	/**
	 * 任务详情
	 * 
	 * @param triggerName
	 * @return
	 */
	QrtzTriggersDto findDetail(String triggerName);

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<QrtzTriggersDto> findPage(Page<QrtzTriggersDto> page, @Param("form") QrtzTriggersDto form);

}