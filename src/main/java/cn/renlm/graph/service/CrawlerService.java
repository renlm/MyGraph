package cn.renlm.graph.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.renlm.graph.modular.crawler.entity.CrawlerRequest;
import cn.renlm.graph.modular.crawler.service.ICrawlerRequestService;
import cn.renlm.graph.properties.CrawlerConfigProperties;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSite;

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
	 * @param siteCodes
	 */
	public void startSites(List<String> siteCodes) {

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
	 * @param url
	 * @param referer
	 * @return
	 */
	public final CrawlerRequest createRequest(boolean save, String siteCode, String siteName, String startUrl,
			String regex, int regexGroup, Integer pageUrlType, int depth, String flag, String url, String referer) {
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
		request.setUrl(url);
		request.setUrlMd5(DigestUtil.md5Hex(url));
		request.setReferer(referer);
		request.setCreatedAt(new Date());
		request.setDeleted(false);
		if (save) {
			iCrawlerRequestService.save(request);
		}
		return request;
	}

}
