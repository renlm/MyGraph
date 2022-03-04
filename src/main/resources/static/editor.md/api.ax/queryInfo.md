> ___接口功能___

该接口用于根据绑定ID或业务标识获取通话信息

> ___接口类型___

| <div style="width:100px;">请求方法</div> | <div style="width:300px;">POST</div> |
| -------- | :----- |
| 访问URI | /queryInfo |

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
| -------- | :----- | :----- | :----- |
| subscriptionId | 否，与userData至少有一个 | String | 绑定ID，唯一标识一组绑定关系 |
| userData | 否，与subscriptionId至少有一个 | String | 业务标识，长度不能超过64位，不能包含中文，不能包含大括号（'{' 或 '}'） |

> ___请求响应___

```json
{
    "statusCode": 200,
    "message": "OK",
    "data":
    [
        {
            "id": 1,
            "ak": "AC5A23CF0E42435C9FF52E401EE897BC",
            "origNum": "+8618801330084",
            "privateNum": "+8616512884987",
            "recordFlag": false,
            "recordHintTone": null,
            "maxDuration": 60,
            "lastMinVoice": null,
            "userData": "1499268874687315968",
            "callDirection": 0,
            "subscriptionId": "88ec9feb-ed7d-4b0a-bf65-cc6be2b9213e",
            "status": 1,
            "businessType": null,
            "bindResponse": "{\"origNum\":\"+8618801330084\",\"resultcode\":\"0\",\"subscriptionId\":\"88ec9feb-ed7d-4b0a-bf65-cc6be2b9213e\",\"privateNum\":\"+8616512884987\",\"resultdesc\":\"Success\"}",
            "unbindResponse": null,
            "bindingDuration": 5,
            "bindTime": 1646288530000,
            "unbindTime": null,
            "sessionId": null,
            "callinTime": null,
            "answerTime": null,
            "disconnectTime": null,
            "recordUrl": null,
            "createdAt": 1646288530000,
            "updatedAt": 1646288530000,
            "remark": null
        }
    ],
    "success": true
}
```

| <div style="width:100px;">参数名称</div> | <div style="width:300px;">说明</div> |
| -------- | :----- |
| statusCode | 响应码 |
| message | 消息内容 |
| data[i].id | 绑定记录（AX模式）-主键ID |
| data[i].ak | 系统标识 |
| data[i].origNum | A号码（真实号码） |
| data[i].privateNum | 绑定的X号码 |
| data[i].recordFlag | 是否需要针对该绑定关系产生的所有通话录音 |
| data[i].recordHintTone | 该参数仅在recordFlag为true时有效。该参数用于设置录音提示音，填写为放音文件名，可在放音文件管理页面查看。不携带该参数表示录音前不播放提示音。注：因隐私协议及运营商管控，录音的呼叫必须携带该参数，否则呼叫会被运营商拦截 |
| data[i].maxDuration | 设置允许单次通话进行的最长时间（默认60），单位为分钟。通话时间从接通被叫的时刻开始计算。取值范围：0~1440，0：系统不主动结束通话，由主被叫双方结束通话。1~1440：当通话时长达到此配置值，系统主动结束通话。不携带时，参数值默认为0 |
| data[i].lastMinVoice | 设置通话剩余最后一分钟时的提示音，填写为放音文件名，可在放音文件管理页面查看。当maxDuration字段设置为非0时此参数有效。不携带该参数表示通话剩余最后一分钟时不放音 |
| data[i].userData | 用户自定义数据（请传业务标识ID，此处限定不超过64位），不允许携带以下字符：“{”，“}”（即大括号），不允许包含中文字符，如果包含中文字符请采用Base64编码 |
| data[i].callDirection | 呼叫方向控制。表示该绑定关系允许的呼叫方向。取值范围如下：0：允许双向呼叫。1：只允许A呼叫X号码。2：只允许其他号码呼叫X号码。如果不携带该参数，系统默认该参数为0 |
| data[i].subscriptionId | 绑定ID，唯一标识一组绑定关系 |
| data[i].status | 绑定状态，-1，绑定失败，0：已解绑，1：绑定中 |
| data[i].businessType | 业务类型（用户自定义，保留参数，未启用） |
| data[i].bindResponse | <a href="https://support.huaweicloud.com/api-PrivateNumber/privatenumber_02_0011.html" target="_blank">绑定接口响应</a> |
| data[i].unbindResponse | <a href="https://support.huaweicloud.com/api-PrivateNumber/privatenumber_02_0012.html" target="_blank">解绑接口响应</a> |
| data[i].bindingDuration | 绑定时长（分钟） |
| data[i].bindTime | 绑定时间 |
| data[i].unbindTime | 解绑时间 |
| data[i].sessionId | 唯一指定一条通话链路的标识ID |
| data[i].callinTime | 拨打时间（呼入） |
| data[i].answerTime | 拨通时间（应答） |
| data[i].disconnectTime | 挂机时间（挂机） |
| data[i].recordUrl | 录音地址 |
| data[i].createdAt | 创建时间 |
| data[i].updatedAt | 更新时间 |
| data[i].remark | 备注 |
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
		public void queryInfo() {
			String Nonce = IdUtil.simpleUUID().toUpperCase();
			String Created = String.valueOf(DateUtil.current());
	
			// 请求参数
			Map<String, Object> paramMap = new LinkedHashMap<>();
			paramMap.put("subscriptionId", "88ec9feb-ed7d-4b0a-bf65-cc6be2b9213e");
			MapUtil.removeNullValue(paramMap);
			paramMap = MapUtil.sort(paramMap);
	
			// 参数Hash并加密（公钥）
			String publicKeyBase64 = FileUtil.readUtf8String(ConstVal.resourcesTestDir + pubPath);
			RSA rsa = new RSA(null, publicKeyBase64);
			String paramMapJson = JSONUtil.toJsonStr(paramMap);
			String Authorization = DigestUtil.sha256Hex(StrUtil.join(StrUtil.COMMA, Ak, Nonce, Created, paramMapJson));
	
			// 请求接口
			HttpRequest httpRequest = HttpUtil.createPost(Host + "/api/ax/queryInfo");
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