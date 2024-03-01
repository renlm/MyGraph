package cn.renlm.graph.amqp;

import static cn.hutool.core.convert.Convert.toInt;
import static cn.hutool.core.convert.Convert.toStr;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Date;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.service.IGraphService;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.service.ISysFileService;
import cn.renlm.graph.mxgraph.ERModelParser;
import cn.renlm.graph.properties.MyConfigProperties;
import cn.renlm.graph.properties.MyConfigProperties.Chrome;
import cn.renlm.graph.util.RedisUtil;
import cn.renlm.plugins.MyCrawlerUtil;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import lombok.extern.slf4j.Slf4j;

/**
 * 图形封面任务
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration
public class GraphCoverQueue {

	private static final String KEY = "GraphCover";

	public static final String EXCHANGE = KEY + AmqpUtil.Exchange;

	public static final String QUEUE = KEY + AmqpUtil.Queue;

	public static final String ROUTINGKEY = QUEUE + AmqpUtil.RoutingKey;

	@Autowired
	private MyConfigProperties myConfigProperties;

	@Autowired
	private IGraphService iGraphService;

	@Autowired
	private ISysFileService iSysFileService;

	/**
	 * 抓取封面
	 * 
	 * @param uuid
	 */
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = QUEUE, durable = Exchange.TRUE), exchange = @Exchange(value = EXCHANGE, type = ExchangeTypes.DIRECT), key = ROUTINGKEY) })
	public void receiveMessage(String key) {
		boolean fitWindow = false;
		RedisTemplate<String, String> edisTemplate = RedisUtil.getRedisTemplate();
		String uuid = edisTemplate.opsForValue().get(key);
		log.info("=== 图形封面任务：{}", uuid);
		if (StrUtil.isBlank(uuid)) {
			return;
		}
		Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, uuid));
		if (graph == null || StrUtil.isBlank(graph.getXml())) {
			return;
		}
		// 设置尺寸
		Rectangle rectangle = ERModelParser.getRectangle(graph.getXml());
		String w = toStr(toInt(Math.ceil(rectangle.getWidth() < 800 ? 800 : rectangle.getWidth())) + 60);
		String h = toStr(toInt(Math.ceil(rectangle.getHeight() < 600 ? 600 : rectangle.getHeight())) + 60);
		Setting chromeSetting = new Setting();
		Chrome chrome = myConfigProperties.getChrome();
		chromeSetting.set("driverPath", chrome.getDriverPath());
		chromeSetting.set("sleepTime", chrome.getSleepTime());
		chromeSetting.set("windowSize", StrUtil.join(StrUtil.COMMA, w, h));
		// 启动爬虫
		String imageType = ImgUtil.IMAGE_TYPE_PNG;
		String originalFilename = StrUtil.join(StrUtil.DOT, graph.getName(), imageType);
		MySite site = MySite.me();
		site.setEnableSelenuim(true);
		site.setHeadless(true);
		site.setScreenshot(true);
		site.setSleepTime(0);
		site.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			BufferedImage screenshot = ImgUtil.toImage(myPage.screenshotBASE64());
			byte[] bytes = ImgUtil.toBytes(screenshot, ImgUtil.IMAGE_TYPE_PNG);
			SysFile sysFile = iSysFileService.upload(originalFilename, bytes, file -> {
				file.setCreatorUserId(graph.getCreatorUserId());
				file.setCreatorNickname(graph.getCreatorNickname());
			});
			// 设置封面
			iGraphService.update(Wrappers.<Graph>lambdaUpdate().func(wrapper -> {
				wrapper.set(Graph::getCover, sysFile.getFileId());
				wrapper.set(Graph::getUpdatedAt, new Date());
				wrapper.set(Graph::getUpdatorUserId, graph.getCreatorUserId());
				wrapper.set(Graph::getUpdatorNickname, graph.getUpdatorNickname());
				wrapper.in(Graph::getUuid, uuid);
			}));
		});
		String path = StrUtil.format("/pub/doc/gtv/{}?headless=true&fitWindow={}&w={}&h={}", key, fitWindow, w, h);
		spider.addUrl(myConfigProperties.getCtx() + path);
		spider.run();
	}

	/**
	 * 声明交换机
	 * 
	 * @return
	 */
	@Bean(name = EXCHANGE)
	public DirectExchange exchange() {
		return ExchangeBuilder.directExchange(EXCHANGE).durable(true).build();
	}

	/**
	 * 声明队列
	 * 
	 * @return
	 */
	@Bean(name = QUEUE)
	public org.springframework.amqp.core.Queue queue() {
		return QueueBuilder.durable(QUEUE).build();
	}

	/**
	 * 绑定队列到交换机
	 * 
	 * @param exchange
	 * @param queue
	 * @return
	 */
	@Bean(name = ROUTINGKEY)
	public Binding binding(@Qualifier(EXCHANGE) DirectExchange exchange,
			@Qualifier(QUEUE) org.springframework.amqp.core.Queue queue) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY);
	}
}