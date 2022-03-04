> ___接口功能___

该接口用于获取隐私保护通话的虚拟电话号码，默认5分钟有效

> ___接口类型___

| <div style="width:100px;">请求方法</div> | <div style="width:300px;">POST</div> |
| -------- | :----- |
| 访问URI | /api/ax/getCalledNum |

> ___环境配置___

| <div style="width:100px;">环境</div> | <div style="width:300px;">地址</div> |
| -------- | :----- |
| 测试IP | http://<span></span>10.0.204.131:18090 |
| 测试HOST | https://<span></span>vpc-test.m2.com.cn |
| 正式 | https://<span></span>vpc.m2.com.cn |

> ___请求参数___

请求Header参数

| <div style="width:100px;">参数名称</div> | <div style="width:60px;">是否必选</div> | <div style="width:80px;">参数类型</div> | <div style="width:300px;">说明</div> |
| -------- | :-----: | :----- | :----- |
| AK | 是 | String | 系统标识 |
| Nonce | 是 | String | 随机Uuid |
| Created | 是 | String | 时间戳（毫秒数） |
| Authorization | 是 | String | 认证参数 |

	Authorization
	RSA加密串，推荐使用Hutool工具包
	rsa.encryptBase64(DigestUtil.sha256Hex(ak,nonce,created,paramMapJson), KeyType.PublicKey)
	paramMapJson = Key有序的Map，使用默认Key排序方式（字母顺序），转Json字符串

请求表单参数

| <div style="width:100px;">参数名称</div> | <div style="width:60px;">是否必选</div> | <div style="width:80px;">参数类型</div> | <div style="width:300px;">说明</div> |
| -------- | :-----: | :----- | :----- |
| origNum | 是 | String | 需要绑定虚拟电话的号码 |
| userData | 否 | String | 业务标识，长度不能超过64位，不能包含中文，不能包含大括号（'{' 或 '}'） |

> ___请求响应___

```json
{
    "statusCode": 200,
    "message": "OK",
    "data":
    {
        "subscriptionId": "88ec9feb-ed7d-4b0a-bf65-cc6be2b9213e",
        "phone": "+8616512884987",
        "duration": 5,
        "expireTime": 1646288830211
    },
    "success": true
}
```

| <div style="width:100px;">参数名称</div> | <div style="width:300px;">说明</div> |
| -------- | :----- |
| statusCode | 响应码 |
| message | 消息内容 |
| data.subscriptionId | 绑定ID，唯一标识一组绑定关系 |
| data.phone | 号码 |
| data.duration | 有效时长（分钟） |
| data.expireTime | 过期时间 |
| success | 接口响应是否成功 |

| <div style="width:100px;">响应码</div> | <div style="width:300px;">说明</div> |
| -------- | :----- |
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 认证失败 |
| 408 | 请求过期 |
| 500 | 服务器异常 |
| 508 | 重复调用 |

> ___接口示例___

接口示例仅供参考，请以实际消息为准。

	package com.lei.du;
	
	import java.util.LinkedHashMap;
	import java.util.Map;
	
	import org.junit.jupiter.api.Test;
	
	import com.lei.du.interceptor.APIHandlerInterceptor;
	
	import cn.hutool.core.date.DateUtil;
	import cn.hutool.core.io.FileUtil;
	import cn.hutool.core.lang.Console;
	import cn.hutool.core.map.MapUtil;
	import cn.hutool.core.util.IdUtil;
	import cn.hutool.core.util.StrUtil;
	import cn.hutool.crypto.asymmetric.KeyType;
	import cn.hutool.crypto.asymmetric.RSA;
	import cn.hutool.crypto.digest.DigestUtil;
	import cn.hutool.http.HttpRequest;
	import cn.hutool.http.HttpResponse;
	import cn.hutool.http.HttpUtil;
	import cn.hutool.json.JSONUtil;
	import cn.renlm.plugins.ConstVal;
	
	/**
	 * 虚拟号码（AX模式）
	 * 
	 * @author Renlm
	 *
	 */
	public class APIAxTest {
	
		final String Host = "http://localhost";
		final String Ak = "AC5A23CF0E42435C9FF52E401EE897BC";
		final String pubPath = "/VirtualPhone.pub";
	
		@Test
		public void getCalledNum() {
			String Nonce = IdUtil.simpleUUID().toUpperCase();
			String Created = String.valueOf(DateUtil.current());
	
			// 请求参数
			Map<String, Object> paramMap = new LinkedHashMap<>();
			paramMap.put("origNum", "18801330084");
			paramMap.put("userData", IdUtil.getSnowflakeNextIdStr());
			MapUtil.removeNullValue(paramMap);
			paramMap = MapUtil.sort(paramMap);
	
			// 参数Hash并加密（公钥）
			String publicKeyBase64 = FileUtil.readUtf8String(ConstVal.resourcesTestDir + pubPath);
			RSA rsa = new RSA(null, publicKeyBase64);
			String paramMapJson = JSONUtil.toJsonStr(paramMap);
			String Authorization = DigestUtil.sha256Hex(StrUtil.join(StrUtil.COMMA, Ak, Nonce, Created, paramMapJson));
	
			// 请求接口
			HttpRequest httpRequest = HttpUtil.createPost(Host + "/api/ax/getCalledNum");
			httpRequest.header(APIHandlerInterceptor.AK, Ak);
			httpRequest.header(APIHandlerInterceptor.Nonce, Nonce);
			httpRequest.header(APIHandlerInterceptor.Created, Created);
			httpRequest.header(APIHandlerInterceptor.Authorization, rsa.encryptBase64(Authorization, KeyType.PublicKey));
			httpRequest.form(paramMap);
			HttpResponse httpResponse = httpRequest.execute();
			if (httpResponse.isOk()) {
				String body = httpResponse.body();
				if (JSONUtil.isJson(body)) {
					Console.log(JSONUtil.formatJsonStr(body));
					return;
				} else {
					Console.log(httpResponse.body());
				}
			} else {
				Console.log(httpResponse.getStatus());
			}
		}
	}