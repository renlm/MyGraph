# Markdown扩展

在<a href="https://github.com/pandao/editor.md" target="_blank"> editor.md </a>基础上进行了功能扩展，新增 **Json文档**、**Layui皮肤** 和 **Echarts图表**。  
___<a href="https://mygraph.renlm.cn/static/markdown/editor.md-1.5.0/examples/custom-extras.html?fullscreen=true" target="_blank"> 在线编辑器 </a>___  
___<a href="https://mygraph.renlm.cn/static/markdown/editor.md-1.5.0/examples/index.html" target="_blank"> 完整示例 </a>___  
___<a href="https://renlm.cn/images/demo/15.png" target="_blank"> 截图-使用示例 </a>___  
___<a href="https://renlm.cn/images/demo/30.png" target="_blank"> 截图-语法示例 </a>___  
___<a href="https://renlm.cn/images/demo/31.png" target="_blank"> 截图-效果图 </a>___  

### Json文档
扩展语法（json5），支持Json5数据规范，增加了Json文档插件按钮。

##### 简洁模式（Json格式化展示）
```json5
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
```

##### 标准模式（带可配置的注释表格）
```json5
{
    /**
     * 配置项（可选）
     */
    $Config:
{
    /**
     * 是否显示Json预览（默认显示）
     */
    showJson: true,
    /**
     * 注释表格显示列（默认全部）
     * type:类型
     * required:是否必须
     * comment:说明
     */
    commentColumns: ['type', 'required', 'comment']
},
    /**
     * 注释表格
     * $Example存在时有效，否则预览代码块Json
     */
    $TypeScript:
{
    'statusCode': {type:'number',required:true,comment:'响应码'},
    'message': {type:'string',required:true,comment:'消息'},
    'data': {type:'object',required:null,comment:'数据项'},
    'data.uuid': {type:'string',required:true,comment:'uuid'},
    'data.name': {type:'string',required:true,comment:'名称'},
    'data.description': {type:'string',required:false,comment:'简介'},
    'success': {type:'boolean',required:true,comment:'是否成功'}
},
    /**
     * 样例数据
     * $TypeScript存在时有效，否则预览代码块Json
     */
    $Example:
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
    /**
     * 应用的主题
     */
    $theme: 'dark',
    /**
     * 可显式指定实例宽度，单位为像素
     * 如果传入值为null/undefined/'auto'，则表示自动取 dom（实例容器）的宽度
     */
    $width: 'auto',
    /**
     * 可显式指定实例高度，单位为像素
     * 如果传入值为null/undefined/'auto'，则表示自动取 dom（实例容器）的宽度
     */
    $height: 400,
    title: {
        left: 30,
        top: 30,
        text: 'ECharts 入门示例'
    },
    tooltip: {},
    legend: {
        top: 30,
        data: ['销量']
    },
    grid: {
        top: 90,
        bottom: 50
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