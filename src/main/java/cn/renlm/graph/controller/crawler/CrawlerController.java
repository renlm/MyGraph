package cn.renlm.graph.controller.crawler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.renlm.graph.properties.CrawlerConfigProperties;
import cn.renlm.graph.properties.CrawlerConfigProperties.CrawlerSite;
import cn.renlm.plugins.MyResponse.Datagrid;

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
	private CrawlerConfigProperties crawlerConfigProperties;

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
	 * @return
	 */
	@ResponseBody
	@GetMapping("/config/sites")
	public Datagrid<CrawlerSite> configSites() {
		List<CrawlerSite> sites = crawlerConfigProperties.getSites();
		return Datagrid.of(sites);
	}

}
