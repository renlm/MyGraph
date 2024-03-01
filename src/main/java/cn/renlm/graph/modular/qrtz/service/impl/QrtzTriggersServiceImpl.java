package cn.renlm.graph.modular.qrtz.service.impl;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.ObjectUtil;
import cn.renlm.graph.modular.qrtz.dto.QrtzTriggersDto;
import cn.renlm.graph.modular.qrtz.mapper.QrtzTriggersMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzTriggersService;
import lombok.SneakyThrows;

/**
 * 触发器
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Service
public class QrtzTriggersServiceImpl implements IQrtzTriggersService {

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private QrtzTriggersMapper qrtzTriggersMapper;

	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public void add(String jobName, Class<? extends QuartzJobBean> jobClass, String cronExpression,
			JobDataMap jobDataMap, String description) {
		JobKey jobKey = new JobKey(jobName, jobClass.getName());
		String triggerName = TriggerKey.createUniqueName(jobKey.getGroup());
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, jobKey.getGroup());

		// 删除已有任务
		scheduler.deleteJob(jobKey);

		// 创建调度作业
		JobBuilder jobBuilder = JobBuilder.newJob(jobClass).withIdentity(jobKey);
		JobDetail jobDetail = jobBuilder.withDescription(description).build();

		// 定义调度触发规则
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey);
		if (ObjectUtil.isNotEmpty(jobDataMap)) {
			triggerBuilder.usingJobData(jobDataMap);
		}
		triggerBuilder.withDescription(description);
		triggerBuilder.startAt(DateBuilder.futureDate(1, IntervalUnit.SECOND));
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
		Trigger trigger = triggerBuilder.startNow().build();

		// 把作业和触发器注册到任务调度中
		scheduler.scheduleJob(jobDetail, trigger);
	}

	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public void update(String triggerName, String cronExpression, JobDataMap jobDataMap, String description) {
		QrtzTriggersDto detail = findDetail(triggerName);
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, detail.getJobGroup());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

		// 更新调度触发规则
		trigger.getJobDataMap().clear();
		TriggerBuilder<CronTrigger> triggerBuilder = trigger.getTriggerBuilder().withIdentity(triggerKey);
		if (ObjectUtil.isNotEmpty(jobDataMap)) {
			triggerBuilder.usingJobData(jobDataMap);
		}
		triggerBuilder.withDescription(description);
		triggerBuilder.startAt(DateBuilder.futureDate(1, IntervalUnit.SECOND));
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
		Trigger newTrigger = triggerBuilder.startNow().build();

		// 重启触发器
		scheduler.rescheduleJob(triggerKey, newTrigger);
	}

	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public void pause(String triggerName) {
		QrtzTriggersDto detail = findDetail(triggerName);
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, detail.getJobGroup());
		scheduler.pauseTrigger(triggerKey);
	}

	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public void resume(String triggerName) {
		QrtzTriggersDto detail = findDetail(triggerName);
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, detail.getJobGroup());
		scheduler.resumeTrigger(triggerKey);
	}

	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public void run(String triggerName) {
		QrtzTriggersDto detail = findDetail(triggerName);
		JobKey jobKey = JobKey.jobKey(detail.getJobName(), detail.getJobGroup());
		scheduler.triggerJob(jobKey, detail.getJobDataMap());
	}

	@Override
	public boolean exists(String jobClassName) {
		return qrtzTriggersMapper.exists(jobClassName);
	}

	@Override
	public QrtzTriggersDto findDetail(String triggerName) {
		return qrtzTriggersMapper.findDetail(triggerName);
	}

	@Override
	public Page<QrtzTriggersDto> findPage(Page<QrtzTriggersDto> page, QrtzTriggersDto form) {
		return qrtzTriggersMapper.findPage(page, form);
	}
}