package cn.renlm.mygraph.modular.qrtz.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.mygraph.modular.qrtz.dto.QrtzTriggersDto;

/**
 * 触发器
 * 
 * @author RenLiMing(任黎明)
 *
 */
public interface QrtzTriggersMapper {

	/**
	 * 任务是否存在
	 * 
	 * @param jobClassName 任务执行类
	 * @return
	 */
	boolean exists(String jobClassName);

	/**
	 * 任务详情
	 * 
	 * @param triggerName 触发器名称
	 * @return
	 */
	QrtzTriggersDto findDetail(String triggerName);

	/**
	 * 分页列表
	 * 
	 * @param page 分页参数
	 * @param form 查询表单
	 * @return
	 */
	Page<QrtzTriggersDto> findPage(Page<QrtzTriggersDto> page, @Param("form") QrtzTriggersDto form);

}