package com.lei.du.interceptor;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lei.du.common.Result;
import com.lei.du.huawei.entity.VirtualPhoneKey;
import com.lei.du.huawei.service.IVirtualPhoneKeyService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import io.netty.util.CharsetUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;

/**
 * 接口验权
 * 
 * @author Renlm
 *
 */
@Component
public class APIHandlerInterceptor implements HandlerInterceptor {

	/**
	 * 请求过期时间（毫秒数）
	 */
	public static final long expired = 300 * 1000;

	/**
	 * 系统标识
	 */
	public static final String AK = "Ak";

	/**
	 * 随机Uuid
	 */
	public static final String Nonce = "Nonce";

	/**
	 * 时间戳（毫秒数）
	 */
	public static final String Created = "Created";

	/**
	 * RSA加密串
	 */
	// DigestUtil.md5Hex(ak,nonce,created,paramMapJson)
	// paramMapJson = Key有序的Map，使用默认Key排序方式（字母顺序），转Json字符串
	public static final String Authorization = "Authorization";

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private IVirtualPhoneKeyService iVirtualPhoneKeyService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String ak = ServletUtil.getHeader(request, AK, CharsetUtil.UTF_8);
		String nonce = ServletUtil.getHeader(request, Nonce, CharsetUtil.UTF_8);
		String created = ServletUtil.getHeader(request, Created, CharsetUtil.UTF_8);
		String authorization = ServletUtil.getHeader(request, Authorization, CharsetUtil.UTF_8);

		// 请求头
		if (StrUtil.isBlank(ak) || StrUtil.isBlank(nonce) || !NumberUtil.isLong(created)
				|| StrUtil.isBlank(authorization)) {
			write(request, response, HttpStatus.BAD_REQUEST);
			return false;
		}

		// 时间戳校验
		if (DateUtil.current() - NumberUtil.parseLong(created) > expired) {
			write(request, response, HttpStatus.REQUEST_TIMEOUT);
			return false;
		}

		// 参数Hash
		Map<String, String> paramMap = ServletUtil.getParamMap(request);
		MapUtil.removeNullValue(paramMap);
		paramMap = MapUtil.sort(paramMap);

		// 获取认证配置
		VirtualPhoneKey vpk = iVirtualPhoneKeyService.getOne(Wrappers.<VirtualPhoneKey>lambdaQuery().func(wrapper -> {
			wrapper.eq(VirtualPhoneKey::getAk, ak);
			wrapper.eq(VirtualPhoneKey::getDisabled, false);
		}));
		if (vpk == null) {
			write(request, response, HttpStatus.UNAUTHORIZED);
			return false;
		}

		// 接口验权
		try {
			String paramMapJson = JSONUtil.toJsonStr(paramMap);
			String token = DigestUtil.md5Hex(StrUtil.join(StrUtil.COMMA, ak, nonce, created, paramMapJson));
			RSA rsa = new RSA(vpk.getPrivateKey(), vpk.getPublicKey());
			String encrypt = rsa.decryptStr(authorization, KeyType.PrivateKey);
			if (!StrUtil.equals(token, encrypt)) {
				write(request, response, HttpStatus.UNAUTHORIZED);
				return false;
			}
			// 禁止接口重复调用
			else {
				String key = DigestUtil.md5Hex(authorization);
				if (StrUtil.isNotBlank(redisTemplate.opsForValue().get(key))) {
					write(request, response, HttpStatus.LOOP_DETECTED);
					return false;
				} else {
					redisTemplate.opsForValue().set(key, token, expired, TimeUnit.MILLISECONDS);
				}
			}
		} catch (Exception e) {
			write(request, response, HttpStatus.UNAUTHORIZED);
			return false;
		}

		return true;
	}

	/**
	 * 输出响应
	 * 
	 * @param request
	 * @param response
	 * @param status
	 */
	@SneakyThrows
	private static final void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status) {
		response.setContentType("application/json;charset=" + request.getCharacterEncoding());
		@Cleanup
		PrintWriter out = response.getWriter();
		out.write(JSONUtil.toJsonStr(Result.of(status)));
	}
}