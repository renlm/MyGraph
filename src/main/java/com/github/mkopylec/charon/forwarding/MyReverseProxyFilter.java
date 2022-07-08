package com.github.mkopylec.charon.forwarding;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		super.doFilterInternal(request, response, filterChain);
	}
}