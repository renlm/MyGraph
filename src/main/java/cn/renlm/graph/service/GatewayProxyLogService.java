package cn.renlm.graph.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.convert.Convert;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dmt.GatewayProxyLogDmt;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyLogDto;
import cn.renlm.graph.modular.gateway.repository.GatewayProxyLogRepository;

/**
 * 网关代理日志
 * 
 * @author Renlm
 *
 */
@Service
public class GatewayProxyLogService {

	@Autowired
	private GatewayProxyLogRepository gatewayProxyLogRepository;

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	public Page<GatewayProxyLogDto> findPage(Page<GatewayProxyLogDto> page, User user, GatewayProxyLogDto form) {
		int pageNo = Convert.toInt(page.getCurrent());
		int pageSize = Convert.toInt(page.getSize());
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		Iterable<GatewayProxyLogDmt> list = gatewayProxyLogRepository.findAll(pageRequest);
		List<GatewayProxyLogDto> records = Convert.toList(GatewayProxyLogDto.class, list);
		return page.setRecords(records);
	}
}