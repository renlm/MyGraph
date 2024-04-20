package cn.renlm.mygraph.oshi;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.hutool.core.thread.ThreadUtil;
import cn.renlm.mygraph.ws.WsMessage;
import cn.renlm.mygraph.ws.WsMessage.WsType;
import cn.renlm.mygraph.ws.WsUtil;

/**
 * 服务器监控
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
public class OshiHandler {

	/**
	 * 采集信息
	 */
	@Scheduled(cron = OshiInfoUtil.cron)
	public void getAndTopic() {
		OshiInfoUtil.collect();
		ThreadUtil.safeSleep(OshiInfoUtil.cronSecond * 1000 / 4);
		WsUtil.topic(WsMessage.build(WsType.oshi, OshiInfoUtil.servers()));
		WsUtil.topic(WsMessage.build(WsType.online, WsUtil.getOnlineUserNumber()));
	}
}