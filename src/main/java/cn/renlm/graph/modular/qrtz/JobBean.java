package cn.renlm.graph.modular.qrtz;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.log.level.Level;
import cn.renlm.graph.modular.qrtz.service.IQrtzLogsService;

/**
 * 任务执行
 * 
 * @author RenLiMing(任黎明)
 *
 */
public abstract class JobBean extends QuartzJobBean {

	protected static final String CONTEXT_KEY = "---JobExecutionContext";

	private final IQrtzLogsService iQrtzLogsService;

	public JobBean(IQrtzLogsService iQrtzLogsService) {
		this.iQrtzLogsService = iQrtzLogsService;
	}

	@Override
	protected final void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
		String batch = IdUtil.simpleUUID().toUpperCase();
		AtomicInteger seq = new AtomicInteger();
		this.log(context, batch, seq, Level.INFO, "开始 . . .");
		try {
			Map<String, Object> wrappedMap = new LinkedHashMap<>();
			wrappedMap.putAll(jobDataMap.getWrappedMap());
			wrappedMap.put(CONTEXT_KEY, context);
			exec(batch, seq, wrappedMap);
			this.log(context, batch, seq, Level.INFO, "完成！");
		} catch (Exception e) {
			e.printStackTrace();
			this.log(context, batch, seq, Level.ERROR, ExceptionUtil.stacktraceToString(e));
			this.log(context, batch, seq, Level.ERROR, "失败！");
		}
	}

	/**
	 * 执行业务处理
	 * 
	 * @param batch
	 * @param seq
	 * @param params
	 */
	public abstract void exec(String batch, AtomicInteger seq, Map<String, Object> params);

	/**
	 * 保存日志
	 * 
	 * @param context
	 * @param batch
	 * @param seq
	 * @param level
	 * @param text
	 */
	protected final void log(JobExecutionContext context, String batch, AtomicInteger seq, Level level, String text) {
		iQrtzLogsService.insert(context, batch, seq.incrementAndGet(), level, text);
	}
}