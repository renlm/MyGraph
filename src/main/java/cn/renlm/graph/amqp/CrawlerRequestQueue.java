package cn.renlm.graph.amqp;

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

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.setting.Setting;
import cn.renlm.graph.dto.CrawlerRequestDto;
import cn.renlm.graph.modular.crawler.entity.CrawlerRequest;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSite;
import cn.renlm.plugins.MyCrawlerUtil;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;

/**
 * 简易爬虫 - 访问请求
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Configuration
public class CrawlerRequestQueue {

	private static final String KEY = "CrawlerRequest";

	public static final String EXCHANGE = KEY + AmqpUtil.Exchange;

	public static final String QUEUE = KEY + AmqpUtil.Queue;

	public static final String ROUTINGKEY = QUEUE + AmqpUtil.RoutingKey;

	@Autowired
	private JedisPool jedisPool;

	/**
	 * 接收消息
	 * 
	 * @param request
	 */
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = QUEUE, durable = Exchange.TRUE), exchange = @Exchange(value = EXCHANGE, type = ExchangeTypes.DIRECT), key = ROUTINGKEY) })
	public void receiveMessage(CrawlerRequestDto param) {
		CrawlerSite site = param.getSite();
		MySite mySite = MySite.me();
		Setting chromeSetting = new Setting("config/chrome.setting");
		mySite.setForceUpdate(BooleanUtil.isTrue(param.getForceUpdate()));
		mySite.setEnableSelenuim(BooleanUtil.isTrue(site.isEnableSelenuim()));
		mySite.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(jedisPool, mySite, myPage -> {

		}, myData -> {

		}).onDownloaded(mySite, page -> {
			Request req = page.getRequest();

			// 下载失败（例：连接超时）
			if (req == null) {
				return;
			}

			// 获取当前链接
			CrawlerRequest crawlerRequest = req.getExtra(KEY);
			if (crawlerRequest == null) {
				return;
			}

			// 更新请求状态、网页标题及截屏图片
			crawlerRequest.setStatusCode(page.getStatusCode());
			crawlerRequest.setHtmlTitle(page.getHtml().xpath("//title/text()").get());
			if (site.isSaveHtml()) {
				crawlerRequest.setHtmlContent(page.getRawText());
			}
			if (site.isScreenshot()) {
				String screenshotBASE64 = req.getExtra(MyCrawlerUtil.screenshotBASE64ExtraKey);
				crawlerRequest.setScreenshotBase64(screenshotBASE64);
			}
			crawlerRequest.setUpdatedAt(new Date());
			page.getRequest().putExtra(KEY, crawlerRequest);
		});
		spider.thread(1);
		spider.setEmptySleepTime(site.getSleepTime());
		spider.addRequest(param.getRequests());
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
