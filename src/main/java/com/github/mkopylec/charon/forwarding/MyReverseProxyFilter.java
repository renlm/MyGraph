package com.github.mkopylec.charon.forwarding;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;

import com.github.mkopylec.charon.configuration.GatewayUtil;
import com.github.mkopylec.charon.configuration.RequestMappingConfiguration;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 反向代理
 * 
 * @author Renlm
 *
 */
public class MyReverseProxyFilter extends ReverseProxyFilter {

	/**
	 * 封装请求
	 * 
	 * @param order
	 * @param requestMappingConfigurations
	 */
	public MyReverseProxyFilter(int order, List<RequestMappingConfiguration> requestMappingConfigurations) {
		super(order, requestMappingConfigurations);
		ReflectUtil.setFieldValue(this, StrUtil.lowerFirst(HttpRequestMapper.class.getSimpleName()),
				new HttpRequestMapper() {
					@Override
					RequestEntity<byte[]> map(HttpServletRequest request) throws IOException {
						RequestEntity<byte[]> requestEntity = super.map(request);
						HttpHeaders headers = HttpHeaders.writableHttpHeaders(requestEntity.getHeaders());
						headers.add(GatewayUtil.HEADER_RemoteAddr, request.getRemoteAddr());
						return requestEntity;
					}
				});
	}

	/**
	 * 清空配置缓存
	 * 
	 * @param filter
	 */
	public static final void clear(ReverseProxyFilter filter) {
		RestTemplateProvider restTemplateProvider = (RestTemplateProvider) ReflectUtil.getFieldValue(filter,
				StrUtil.lowerFirst(RestTemplateProvider.class.getSimpleName()));
		ConcurrentMap<?, ?> restTemplates = (ConcurrentMap<?, ?>) ReflectUtil.getFieldValue(restTemplateProvider,
				"restTemplates");
		restTemplates.clear();
	}
}