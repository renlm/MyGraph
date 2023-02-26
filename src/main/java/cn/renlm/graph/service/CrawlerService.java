package cn.renlm.graph.service;

import static cn.renlm.graph.amqp.CrawlerRequestQueue.EXCHANGE;
import static cn.renlm.graph.amqp.CrawlerRequestQueue.QUEUE;
import static cn.renlm.graph.amqp.CrawlerRequestQueue.ROUTINGKEY;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.amqp.AmqpUtil;
import cn.renlm.graph.dto.CrawlerRequestDto;
import cn.renlm.graph.modular.crawler.entity.CrawlerRequest;
import cn.renlm.graph.modular.crawler.service.ICrawlerRequestService;
import cn.renlm.graph.properties.CrawlerConfigProperties;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSite;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSiteEndpoint;
import us.codecraft.webmagic.Request;

/**
 * 简易爬虫
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Service
public class CrawlerService {

	@Autowired
	private CrawlerConfigProperties crawlerConfigProperties;

	@Autowired
	private ICrawlerRequestService iCrawlerRequestService;

	/**
	 * 爬虫站点
	 * 
	 * @param siteCode
	 * @return
	 */
	public CrawlerSite getSiteByCode(String siteCode) {
		List<CrawlerSite> sites = crawlerConfigProperties.getSites();
		if (sites == null) {
			return null;
		}
		for (CrawlerSite site : sites) {
			if (StrUtil.equals(siteCode, site.getCode())) {
				return site;
			}
		}
		return null;
	}

	/**
	 * 爬虫站点
	 * 
	 * @param keywords
	 * @return
	 */
	public List<CrawlerSite> getSites(String keywords) {
		List<CrawlerSite> sites = crawlerConfigProperties.getSites();
		if (sites == null) {
			return CollUtil.newArrayList();
		}
		if (StrUtil.isBlank(keywords)) {
			return sites;
		}
		return sites.stream().filter(
				site -> StrUtil.contains(site.getCode(), keywords) || StrUtil.contains(site.getName(), keywords))
				.collect(Collectors.toList());
	}

	/**
	 * 启动站点
	 * 
	 * @param forceUpdate 是否强制更新
	 * @param siteCodes   站点代码集
	 */
	public void startSites(boolean forceUpdate, List<String> siteCodes) {
		List<CrawlerSite> sites = crawlerConfigProperties.getSites();
		if (CollUtil.isNotEmpty(sites)) {
			for (CrawlerSite site : sites) {
				if (CollUtil.contains(siteCodes, site.getCode())) {
					List<CrawlerSiteEndpoint> endpoints = site.getEndpoints();
					if (CollUtil.isNotEmpty(endpoints)) {
						for (CrawlerSiteEndpoint endpoint : endpoints) {
							List<String> flags = endpoint.getFlag();
							CollUtil.removeBlank(flags);
							String siteCode = site.getCode();
							String siteName = site.getName();
							String startUrl = endpoint.getStartUrl();
							String regex = endpoint.getRegex();
							int regexGroup = endpoint.getRegexGroup();
							Integer pageUrlType = endpoint.getPageUrlType();
							int depth = endpoint.getDepth();
							String flag = JSONUtil.toJsonStr(flags);
							// 保存访问请求
							CrawlerRequest crawlerRequest = this.createRequest(true, 
									siteCode, 
									siteName, 
									startUrl,
									regex, 
									regexGroup, 
									pageUrlType, 
									depth, 
									flag);
							// 添加队列任务
							CrawlerRequestDto newRequest = new CrawlerRequestDto();
							Request request = new Request(startUrl);
							request.putExtra(QUEUE, crawlerRequest);
							newRequest.setForceUpdate(forceUpdate);
							newRequest.setSiteCode(siteCode);
							newRequest.setRequests(new Request[] { request });
							AmqpUtil.createQueue(EXCHANGE, ROUTINGKEY, newRequest);
						}
					}
				}
			}
		}
	}

	/**
	 * 新建访问请求
	 * 
	 * @param save
	 * @param siteCode
	 * @param siteName
	 * @param startUrl
	 * @param regex
	 * @param regexGroup
	 * @param pageUrlType
	 * @param depth
	 * @param flag
	 * @return
	 */
	public final CrawlerRequest createRequest(boolean save, String siteCode, String siteName, String startUrl,
			String regex, int regexGroup, Integer pageUrlType, int depth, String flag) {
		CrawlerRequest request = new CrawlerRequest();
		request.setId(IdUtil.getSnowflakeNextId());
		request.setSiteCode(siteCode);
		request.setSiteName(siteName);
		request.setStartUrl(startUrl);
		request.setRegex(regex);
		request.setRegexGroup(regexGroup);
		request.setPageUrlType(pageUrlType);
		request.setDepth(depth);
		request.setFlag(flag);
		request.setCreatedAt(new Date());
		request.setDeleted(false);
		if (save) {
			iCrawlerRequestService.save(request);
		}
		return request;
	}

}
