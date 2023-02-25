package cn.renlm.graph.modular.crawler.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.crawler.entity.CrawlerRequest;
import cn.renlm.graph.modular.crawler.mapper.CrawlerRequestMapper;
import cn.renlm.graph.modular.crawler.service.ICrawlerRequestService;

/**
 * <p>
 * 简易爬虫 - 访问请求 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2023-02-25
 */
@Service
public class CrawlerRequestServiceImpl extends ServiceImpl<CrawlerRequestMapper, CrawlerRequest> implements ICrawlerRequestService {

}
