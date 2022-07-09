package cn.renlm.graph.modular.gateway.service.impl;

import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;
import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.renlm.graph.dto.EchartsXyAxis;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyLogDto;
import cn.renlm.graph.modular.gateway.dto.GatewayStatisticalDataDto;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;
import cn.renlm.graph.modular.gateway.mapper.GatewayProxyLogMapper;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyConfigService;
import cn.renlm.graph.modular.gateway.service.IGatewayProxyLogService;

/**
 * <p>
 * 网关代理日志 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-07-08
 */
@Service
public class GatewayProxyLogServiceImpl extends ServiceImpl<GatewayProxyLogMapper, GatewayProxyLog>
		implements IGatewayProxyLogService {

	@Autowired
	private IGatewayProxyConfigService iGatewayProxyConfigService;

	@Override
	public Page<GatewayProxyLog> findPage(Page<GatewayProxyLog> page, User user, GatewayProxyLogDto form) {
		GatewayProxyConfig proxyConfig = iGatewayProxyConfigService.getOne(
				Wrappers.<GatewayProxyConfig>lambdaQuery().eq(GatewayProxyConfig::getUuid, form.getProxyConfigUuid()));
		String sort = form.getSort();
		String order = form.getOrder();
		return this.page(page, Wrappers.<GatewayProxyLog>lambdaQuery().func(wrapper -> {
			if (form.getRequestTime() != null) {
				wrapper.ge(GatewayProxyLog::getRequestTime, form.getRequestTime());
			}
			if (form.getTakeTime() != null) {
				wrapper.ge(GatewayProxyLog::getTakeTime, form.getTakeTime());
			}
			if (form.getStatusCode() != null) {
				wrapper.eq(GatewayProxyLog::getStatusCode, form.getStatusCode());
			}
			wrapper.eq(GatewayProxyLog::getProxyConfigId, proxyConfig.getProxyConfigId());
			wrapper.orderBy("requestTime".equals(sort), "asc".equals(order), GatewayProxyLog::getRequestTime);
			wrapper.orderBy("takeTime".equals(sort), "asc".equals(order), GatewayProxyLog::getTakeTime);
			wrapper.orderByDesc(GatewayProxyLog::getId);
		}));
	}

	@Override
	public GatewayStatisticalDataDto getStatisticalData(String proxyConfigUuid) {
		GatewayStatisticalDataDto statisticalData = new GatewayStatisticalDataDto();
		Integer max = Integer.valueOf(DateUtil.format(new Date(), PURE_DATE_PATTERN));
		BigDecimal zero = new BigDecimal(0);
		Integer min = max;
		// 访问用户数
		Map<String, EchartsXyAxis> uvMap = new HashMap<>();
		List<EchartsXyAxis> uvs = this.baseMapper.getUvStatisticalData(proxyConfigUuid);
		if (CollUtil.isNotEmpty(uvs)) {
			for (EchartsXyAxis data : uvs) {
				Integer key = Integer.valueOf(data.getXAxis());
				uvMap.put(data.getXAxis(), data);
				min = Math.min(min, key);
			}
		}
		// 页面访问量
		Map<String, EchartsXyAxis> pvMap = new HashMap<>();
		List<EchartsXyAxis> pvs = this.baseMapper.getPvStatisticalData(proxyConfigUuid);
		if (CollUtil.isNotEmpty(pvs)) {
			for (EchartsXyAxis data : pvs) {
				Integer key = Integer.valueOf(data.getXAxis());
				pvMap.put(data.getXAxis(), data);
				min = Math.min(min, key);
			}
		}
		// 组装数据
		Date start = DateUtil.parse(String.valueOf(min), PURE_DATE_PATTERN);
		Date end = DateUtil.parse(String.valueOf(max), PURE_DATE_PATTERN);
		List<DateTime> ranges = DateUtil.rangeToList(start, end, DateField.DAY_OF_MONTH);
		for (DateTime dateTime : ranges) {
			String day = DateUtil.format(dateTime, PURE_DATE_PATTERN);
			statisticalData.getDayXAxis().add(DateUtil.format(dateTime, NORM_DATE_PATTERN));
			statisticalData.getUvYAxis().add(uvMap.containsKey(day) ? uvMap.get(day).getYAxis() : zero);
			statisticalData.getPvYAxis().add(pvMap.containsKey(day) ? pvMap.get(day).getYAxis() : zero);
		}
		return statisticalData;
	}
}
