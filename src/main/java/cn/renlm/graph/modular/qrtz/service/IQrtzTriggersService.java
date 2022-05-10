package cn.renlm.graph.modular.qrtz.service;

import java.util.Map;

import org.springframework.scheduling.quartz.QuartzJobBean;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.modular.qrtz.dto.QrtzTriggersDto;

/**
 * 触发器
 * 
 * @author Renlm
 *
 */
public interface IQrtzTriggersService {

	/**
	 * 新建
	 *
	 * @param jobName        任务名称
	 * @param jobClass       任务实现类
	 * @param cronExpression 时间表达式 （如：0/5 * * * * ? ）
	 * @param params         任务参数
	 * @param description    任务描述
	 */
	void add(String jobName, Class<? extends QuartzJobBean> jobClass, String cronExpression, Map<String, Object> params,
			String description);

	/**
	 * 修改
	 * 
	 * @param triggerName    触发器名称
	 * @param cronExpression 时间表达式 （如：0/5 * * * * ? ）
	 * @param params         任务参数
	 * @param description    任务描述
	 */
	void update(String triggerName, String cronExpression, Map<String, Object> params, String description);

	/**
	 * 停止
	 * 
	 * @param triggerName 触发器名称
	 */
	void pause(String triggerName);

	/**
	 * 恢复
	 * 
	 * @param triggerName 触发器名称
	 */
	void resume(String triggerName);

	/**
	 * 执行（一次）
	 * 
	 * @param triggerName 触发器名称
	 */
	void run(String triggerName);

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
	 * @param triggerName 触发器名称
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
	Page<QrtzTriggersDto> findPage(Page<QrtzTriggersDto> page, QrtzTriggersDto form);

}