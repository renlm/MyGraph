package cn.renlm.graph.controller.crawler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSite;
import cn.renlm.graph.service.CrawlerService;
import cn.renlm.plugins.MyResponse.Datagrid;
import cn.renlm.plugins.MyResponse.Result;

/**
 * 简易爬虫
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping("/crawler")
public class CrawlerController {

	@Autowired
	private CrawlerService crawlerService;

	/**
	 * 爬虫配置
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/config")
	public String config(ModelMap model) {
		return "crawler/config";
	}

	/**
	 * 爬虫站点
	 * 
	 * @param keywords
	 * @return
	 */
	@ResponseBody
	@GetMapping("/config/sites")
	public Datagrid<CrawlerSite> configSites(String keywords) {
		List<CrawlerSite> sites = crawlerService.getSites(keywords);
		return Datagrid.of(sites);
	}

	/**
	 * 启动站点
	 * 
	 * @param siteCodes
	 * @return
	 */
	@ResponseBody
	@PostMapping("/startSites")
	public Result<?> startSites(String siteCodes) {
		crawlerService.startSites(StrUtil.splitTrim(siteCodes, StrUtil.COMMA));
		return Result.success();
	}

}
