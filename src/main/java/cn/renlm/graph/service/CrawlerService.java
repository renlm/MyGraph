package cn.renlm.graph.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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

}
