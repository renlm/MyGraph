package cn.renlm.graph.amqp;

import static cn.hutool.core.text.CharSequenceUtil.EMPTY;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.setting.Setting;
import cn.renlm.graph.dto.CrawlerRequestDto;
import cn.renlm.graph.modular.crawler.entity.CrawlerRequest;
import cn.renlm.graph.modular.crawler.service.ICrawlerRequestService;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSite;
import cn.renlm.graph.service.CrawlerService;
import cn.renlm.plugins.MyCrawlerUtil;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import cn.renlm.plugins.MyCrawler.PageUrlType;
import cn.renlm.plugins.MyCrawler.scheduler.MyDuplicateVerify;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.BeanUtil;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 简易爬虫 - 访问请求
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration
public class CrawlerRequestQueue {

	public static final String EXTRA_ID = "_CrawlerRequestId";
	public static final String EXTRA_INFO = "_CrawlerRequestInfo";
	public static final String EXTRA_NEXTS = "_CrawlerRequestNexts";

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

		List<Class<?>> scripts = CollUtil.newArrayList();

		MySite mySite = MySite.me();
		Setting chromeSetting = new Setting("config/chrome.setting");
		mySite.setForceUpdate(param.isForceUpdate());
		mySite.setEnableSelenuim(site.isEnableSelenuim());
		mySite.setHeadless(true);
		mySite.setScreenshot(site.isScreenshot());
		mySite.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(jedisPool, mySite, myPage -> {
			Page page = myPage.page();
			String originUrl = page.getRequest().getUrl();
			String url = PageUrlType.standardUrl(originUrl, site.isCleanParams(), site.getInvalidParamNames());

			// 获取当前链接
			CrawlerRequest crawlerRequest = page.getRequest().getExtra(EXTRA_INFO);
			if (crawlerRequest == null) {
				return;
			}

			// 辅助字段
			Integer depth = ObjectUtil.defaultIfNull(crawlerRequest.getDepth(), 1);

			// 解析数据
			if (PageUrlType.data.value() == crawlerRequest.getPageUrlType()) {
				log.info("解析数据：{}", url);
				try {
					for (Class<?> script : scripts) {
						Method main = ReflectUtil.getPublicMethod(script, "main");
						ReflectUtil.invokeStatic(main, myPage, crawlerRequest);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Url自动发现
			int i = 0;
			List<Request> nextRequests = CollUtil.newArrayList();

			// 层级控制
			crawlerRequest.setDepth(ObjectUtil.defaultIfNull(crawlerRequest.getDepth(), 0));
			if (crawlerRequest.getDepth() > 0 && depth >= crawlerRequest.getDepth()) {
				return;
			}
			// 匹配链接
			Map<String, Request> map = new LinkedHashMap<>();
			List<String> urls = this.findUrls(page, crawlerRequest);
			// 加入下层爬取
			for (String nextUrl : urls) {
				// 避免相同网页重复执行
				String standardUrl = PageUrlType.standardUrl(nextUrl, site.isCleanParams(),
						site.getInvalidParamNames());
				if (map.containsKey(standardUrl) || StrUtil.equals(standardUrl, url)) {
					continue;
				}
				// 封装下层执行链接
				CrawlerRequest nextCrawlerRequest = BeanUtil.copy(crawlerRequest, CrawlerRequest.class);
				nextCrawlerRequest.setUrl(standardUrl);
				nextCrawlerRequest.setUrlMd5(DigestUtil.md5Hex(standardUrl));
				nextCrawlerRequest.setDepth(depth + 1);
				Request nextRequest = new Request(standardUrl);
				nextRequest.setPriority(page.getRequest().getPriority() - nextCrawlerRequest.getDepth() * 1000 - i++);
				nextRequest.putExtra(MyCrawlerUtil.depthExtraKey, nextCrawlerRequest.getDepth());
				nextRequest.putExtra(EXTRA_INFO, nextCrawlerRequest);
				nextRequest.putExtra(EXTRA_ID, nextCrawlerRequest.getId());
				nextRequest.putExtra(PageUrlType.extraKey, nextCrawlerRequest.getPageUrlType());
				nextRequests.add(nextRequest);
				map.put(standardUrl, nextRequest);
			}

			// 扩展传递
			page.getRequest().putExtra(EXTRA_NEXTS, nextRequests);
		}, myData -> {
			Task task = myData.task();
			ResultItems resultItems = myData.resultItems();
			MyDuplicateVerify duplicateVerify = myData.duplicateVerify();

			// 过滤重复请求
			List<Request> noDuplicates = CollUtil.newArrayList();
			List<CrawlerRequest> requests = CollUtil.newArrayList();
			List<Request> nextRequests = resultItems.getRequest().getExtra(EXTRA_NEXTS);
			if (CollUtil.isEmpty(nextRequests)) {
				return;
			}
			nextRequests.forEach(nr -> {
				if (!duplicateVerify.verifyDuplicate(param.isForceUpdate(), nr, task)) {
					noDuplicates.add(nr);
					requests.add((CrawlerRequest) nr.getExtras().remove(EXTRA_INFO));
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
			CrawlerRequest crawlerRequest = iCrawlerRequestService.getById(req.getExtra(EXTRA_ID));
			if (crawlerRequest == null) {
				return;
			}

			// 更新请求状态
			// 保存网页、标题及截屏图片
			String htmlTitle = page.getHtml().xpath("//title/text()").get();
			String htmlContent = page.getRawText();
			String screenshotBASE64 = req.getExtra(MyCrawlerUtil.screenshotBASE64ExtraKey);
			crawlerRequest.setStatusCode(page.getStatusCode());
			crawlerRequest.setHtmlTitle(htmlTitle);
			crawlerRequest.setHtmlContent(site.isSaveHtml() ? htmlContent : EMPTY);
			crawlerRequest.setScreenshotBase64(site.isScreenshot() ? screenshotBASE64 : EMPTY);
			crawlerRequest.setUpdatedAt(new Date());
			iCrawlerRequestService.updateById(crawlerRequest);
			page.getRequest().putExtra(EXTRA_INFO, crawlerRequest);
		});
		spider.thread(1);
		spider.setEmptySleepTime(Convert.toLong(site.getSleepTime(), 1000L));
		spider.addRequest(param.getRequests());
		spider.run();
	}

	/**
	 * 查找链接
	 * 
	 * @param page
	 * @param crawlerRequest
	 * @return
	 */
	private List<String> findUrls(Page page, CrawlerRequest crawlerRequest) {
		String regex = crawlerRequest.getRegex();
		int group = ObjectUtil.defaultIfNull(crawlerRequest.getRegexGroup(), 0);
		List<String> urls = page.getHtml().links().regex(regex, group).all().stream().distinct()
				.collect(Collectors.toList());
		return urls;
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
