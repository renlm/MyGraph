package cn.renlm.graph.jobs;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import cn.hutool.log.level.Level;
import cn.renlm.graph.modular.qrtz.JobBean;
import cn.renlm.graph.modular.qrtz.service.IQrtzLogsService;

/**
 * 定时任务Demo（0/5 * * * * ?）
 * 
 * @author Renlm
 *
 */
@Component
public class DemoJob extends JobBean {

	public DemoJob(IQrtzLogsService iQrtzLogsService) {
		super(iQrtzLogsService);
	}

	@Override
	public void exec(String batch, AtomicInteger seq, Map<String, Object> params) {
		JobExecutionContext context = (JobExecutionContext) params.get(CONTEXT_KEY);
		this.log(context, batch, seq, Level.INFO, "定时任务Demo");
	}
}