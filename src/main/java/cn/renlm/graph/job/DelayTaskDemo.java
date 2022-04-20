package cn.renlm.graph.job;

import cn.hutool.json.JSONUtil;
import cn.renlm.graph.amqp.AmqpUtil.DelayTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 延时任务（演示）
 * 
 * @author Renlm
 *
 */
@Slf4j
public class DelayTaskDemo implements DelayTask {

	@Override
	public void execute(String paramJson) {
		log.info("延时任务（演示）：" + JSONUtil.toJsonPrettyStr(paramJson));
	}
}