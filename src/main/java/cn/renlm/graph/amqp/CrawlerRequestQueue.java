package cn.renlm.graph.amqp;

import java.util.Date;
import java.util.List;

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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.setting.Setting;
import cn.renlm.graph.dto.CrawlerRequestDto;
import cn.renlm.graph.modular.crawler.entity.CrawlerRequest;
import cn.renlm.graph.modular.crawler.service.ICrawlerRequestService;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSite;
import cn.renlm.graph.service.CrawlerService;
import cn.renlm.plugins.MyCrawlerUtil;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import cn.renlm.plugins.MyCrawler.scheduler.MyDuplicateVerify;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

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

	@Autowired
	private CrawlerService crawlerService;

	@Autowired
	private ICrawlerRequestService iCrawlerRequestService;

	/**
	 * 接收消息
	 * 
	 * @param param
	 */
	@RabbitListener(concurrency = "#{crawlerConfigProperties.threadNum}", bindings = {
			@QueueBinding(value = @Queue(value = QUEUE, durable = Exchange.TRUE), exchange = @Exchange(value = EXCHANGE, type = ExchangeTypes.DIRECT), key = ROUTINGKEY) })
	public void receiveMessage(CrawlerRequestDto param) {
		CrawlerSite site = crawlerService.getSiteByCode(param.getSiteCode());
		if (site == null) {
			return;
		}
		MySite mySite = MySite.me();
		Setting chromeSetting = new Setting("config/chrome.setting");
		mySite.setForceUpdate(BooleanUtil.isTrue(param.getForceUpdate()));
		mySite.setEnableSelenuim(BooleanUtil.isTrue(site.isEnableSelenuim()));
		mySite.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(jedisPool, mySite, myPage -> {

		}, myData -> {
			Task task = myData.task();
			ResultItems resultItems = myData.resultItems();
			MyDuplicateVerify duplicateVerify = myData.duplicateVerify();

			// 过滤重复请求
			List<Request> noDuplicates = CollUtil.newArrayList();
			List<CrawlerRequest> requests = CollUtil.newArrayList();
			List<Request> nextRequests = resultItems.getRequest().getExtra(EXCHANGE);
			if (CollUtil.isEmpty(nextRequests)) {
				return;
			}
			nextRequests.forEach(nr -> {
				if (!duplicateVerify.verifyDuplicate(param.getForceUpdate(), nr, task)) {
					noDuplicates.add(nr);
					requests.add((CrawlerRequest) nr.getExtras().remove(KEY));
				}
			});

			// 批量保存
			if (CollUtil.isNotEmpty(requests)) {
				iCrawlerRequestService.saveBatch(requests);
				// 添加任务
				CrawlerRequestDto newRequest = new CrawlerRequestDto();
				newRequest.setRequests(noDuplicates.toArray(new Request[noDuplicates.size()]));
				AmqpUtil.createQueue(EXCHANGE, ROUTINGKEY, newRequest);
			}
		}).onDownloaded(mySite, page -> {
			Request req = page.getRequest();

			// 下载失败
			if (req == null) {
				return;
			}

			// 访问请求
			CrawlerRequest crawlerRequest = req.getExtra(QUEUE);
			if (crawlerRequest == null) {
				return;
			}

			// 更新请求状态
			// 保存网页、标题及截屏图片
			page.getRequest().putExtra(QUEUE, crawlerRequest);
			String screenshotBASE64 = req.getExtra(MyCrawlerUtil.screenshotBASE64ExtraKey);
			iCrawlerRequestService.update(Wrappers.<CrawlerRequest>lambdaUpdate().func(wrapper -> {
				wrapper.set(CrawlerRequest::getStatusCode, page.getStatusCode());
				wrapper.set(CrawlerRequest::getHtmlTitle, page.getHtml().xpath("//title/text()").get());
				wrapper.set(site.isSaveHtml(), CrawlerRequest::getHtmlContent, page.getRawText());
				wrapper.set(site.isScreenshot(), CrawlerRequest::getScreenshotBase64, screenshotBASE64);
				wrapper.set(CrawlerRequest::getUpdatedAt, new Date());
				wrapper.eq(CrawlerRequest::getId, crawlerRequest.getId());
			}));
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
