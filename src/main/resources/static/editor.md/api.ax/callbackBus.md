> ___功能简介___

用户通话结束后，<a href="https://support.huaweicloud.com/api-PrivateNumber/privatenumber_02_0006_1a.html" target="_blank">华为隐私保护通话平台</a> 通过此接口向客户推送通话的话单信息，接收话单信息并处理后，系统会根据预设业务回调地址推送通话信息（无论成功与否，只调用一次）

> ___接口类型___

| <div style="width:100px;">请求方法</div> | <div style="width:300px;">POST</div> |
| -------- | :----- |
| 推送地址 | 预设业务回调接口 |

> ___请求Body参数说明___


```json
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
```

| <div style="width:100px;">参数名称</div> | <div style="width:300px;">说明</div> |
| -------- | :----- |
| id | 绑定记录（AX模式）-主键ID |
| ak | 系统标识 |
| origNum | A号码（真实号码） |
| privateNum | 绑定的X号码 |
| recordFlag | 是否需要针对该绑定关系产生的所有通话录音 |
| recordHintTone | 该参数仅在recordFlag为true时有效。该参数用于设置录音提示音，填写为放音文件名，可在放音文件管理页面查看。不携带该参数表示录音前不播放提示音。注：因隐私协议及运营商管控，录音的呼叫必须携带该参数，否则呼叫会被运营商拦截 |
| maxDuration | 设置允许单次通话进行的最长时间（默认60），单位为分钟。通话时间从接通被叫的时刻开始计算。取值范围：0~1440，0：系统不主动结束通话，由主被叫双方结束通话。1~1440：当通话时长达到此配置值，系统主动结束通话。不携带时，参数值默认为0 |
| lastMinVoice | 设置通话剩余最后一分钟时的提示音，填写为放音文件名，可在放音文件管理页面查看。当maxDuration字段设置为非0时此参数有效。不携带该参数表示通话剩余最后一分钟时不放音 |
| userData | 用户自定义数据（请传业务标识ID，此处限定不超过64位），不允许携带以下字符：“{”，“}”（即大括号），不允许包含中文字符，如果包含中文字符请采用Base64编码 |
| callDirection | 呼叫方向控制。表示该绑定关系允许的呼叫方向。取值范围如下：0：允许双向呼叫。1：只允许A呼叫X号码。2：只允许其他号码呼叫X号码。如果不携带该参数，系统默认该参数为0 |
| subscriptionId | 绑定ID，唯一标识一组绑定关系 |
| status | 绑定状态，-1，绑定失败，0：已解绑，1：绑定中 |
| businessType | 业务类型（用户自定义，保留参数，未启用） |
| bindResponse | <a href="https://support.huaweicloud.com/api-PrivateNumber/privatenumber_02_0011.html" target="_blank">绑定接口响应</a> |
| unbindResponse | <a href="https://support.huaweicloud.com/api-PrivateNumber/privatenumber_02_0012.html" target="_blank">解绑接口响应</a> |
| bindingDuration | 绑定时长（分钟） |
| bindTime | 绑定时间 |
| unbindTime | 解绑时间 |
| sessionId | 唯一指定一条通话链路的标识ID |
| callinTime | 拨打时间（呼入） |
| answerTime | 拨通时间（应答） |
| disconnectTime | 挂机时间（挂机） |
| recordUrl | 录音地址 |
| createdAt | 创建时间 |
| updatedAt | 更新时间 |