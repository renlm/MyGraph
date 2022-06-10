# Markdown扩展

在<a href="https://github.com/pandao/editor.md" target="_blank"> editor.md </a>基础上进行了功能扩展，新增 **Json文档**、**Layui皮肤** 和 **Echarts图表**。
___<a href="https://renlm.cn/images/demo/15.png" target="_blank"> 使用示例 </a>___
___<a href="https://renlm.cn/images/demo/30.png" target="_blank"> 效果图 </a>___

### Json文档
扩展语法（json5），支持Json5数据规范，增加了Json文档插件按钮。
```json5
{
    $TypeScript: /*注释文档*/
	{
		'statusCode': {type:'number',required:true,comment:'响应码'},
		'message': {type:'string',required:true,comment:'消息'},
		'data': {type:'object',required:null,comment:'数据项'},
		'data.uuid': {type:'string',required:true,comment:'uuid'},
		'data.name': {type:'string',required:true,comment:'名称'},
		'data.description': {type:'string',required:false,comment:'备注'},
		'success': {type:'boolean',required:true,comment:'是否成功'}
	},
    $Example: /*样例数据*/
	{
		statusCode: 200,
		message: 'OK',
		data: {
			uuid: '32FBBF6C0D31402881CCF30FAEC19E2F',
			name: '系统简介',
			description: null,
		},
		success: true,
	}
}
```
### Layui皮肤
```layui
<blockquote class="layui-elem-quote" style="margin-top: 30px;">
  扩展语法（layui），支持Layui皮肤，丰富展现效果。
</blockquote>
```

### Echarts图表
扩展语法（echarts）
```echarts
{
	$theme: 'dark',
	$width: 'auto',
	$height: 400,
	title: {
		text: 'ECharts 入门示例'
	},
	tooltip: {},
	legend: {
		data: ['销量']
	},
	xAxis: {
		data: ['衬衫', '羊毛衫', '雪纺衫', '裤子', '高跟鞋', '袜子']
	},
	yAxis: {},
	series: [
		{
			name: '销量',
			type: 'bar',
			data: [5, 20, 36, 10, 10, 20]
		}
	]
}
```