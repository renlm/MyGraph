## 简介
MyGraph 在线文档，集成语法增强版Markdown、在线画图、数据库设计、在线数据表格，由一套完整的权限模型控制，支持团队协作、版本控制与分享。  

## 产品体验
体验地址：<a href="https://mygraph.renlm.cn/" target="_blank">https://<span></span>mygraph.renlm.cn/</a>  
账号：S-renyy  
密码（每5分钟重置一次）：Aac^123654.  
<a href="https://mygraph.renlm.cn/pub/doc/s/E42FB06DEC2545D6AC2947C5BF3DC750" target="_blank">系统手册</a>  

## 功能清单
### Markdown文档
对语法和功能进行了增强扩展，新增独有的Json文档，扩展了Layui皮肤、Echarts图表、附件上传等功能。  
___<a href="https://mygraph.renlm.cn/static/markdown/editor.md-1.5.0/examples/custom-extras.html?fullscreen=true" target="_blank"> 在线编辑器 </a>___  
___<a href="https://mygraph.renlm.cn/static/markdown/editor.md-1.5.0/examples/index.html" target="_blank"> 完整示例 </a>___  
### 在线画图
支持流程图、UML、思维导图等常见图形。  
自动计算图形尺寸，生成封面图片。  
### 数据库设计
支持从数据源选择表生成ER图，导出DDL语句。  
无论是新系统的设计，还是梳理老项目的数据结构，都是十分犀利的工具。  
目前支持MySQL、PostgreSQL，可轻松扩展其它类型的数据库支持。  
自动计算图形尺寸，生成封面图片。  
### 在线数据表格
支持xlsx格式的Excel文件。  
### 权限管理
完整的后台权限设计，对数据安全的进行了增强。  
### 服务器实时监控
提供两种风格的服务器运行状态的实时监控。  
### 团队协作
支持团队协作、版本控制与分享，分享可以选择设置有效期和密码。  

## 功能截图
### 工作台
#### 在线画图
##### 图形设计
![图形设计](https://renlm.cn/images/demo/100101.png "图形设计")
##### 数据源
![数据源](https://renlm.cn/images/demo/100102.png "数据源")
#### 文档管理
##### 知识文库
###### 我的项目
![我的项目](https://renlm.cn/images/demo/100201.png "我的项目")
###### 文档-Markdown
![Markdown文档](https://renlm.cn/images/demo/100201101.png "Markdown文档")
###### 文档-图形设计
![图形设计](https://renlm.cn/images/demo/100201102.png "图形设计")
###### 收藏
![收藏](https://renlm.cn/images/demo/100202.png "收藏")
###### 分享
![分享](https://renlm.cn/images/demo/100203.png "分享")
###### 历史记录
![历史记录](https://renlm.cn/images/demo/100204.png "历史记录")
### 系统管理
#### 权限管理
##### 用户管理
![用户管理](https://renlm.cn/images/demo/200101.png "用户管理")
##### 组织机构
![组织机构](https://renlm.cn/images/demo/200102.png "组织机构")
##### 角色管理
![角色管理](https://renlm.cn/images/demo/200103.png "角色管理")
##### 登录日志
![登录日志](https://renlm.cn/images/demo/200104.png "登录日志")
#### 数据字典
##### 通用字典
![通用字典](https://renlm.cn/images/demo/200201.png "通用字典")
##### 系统常量
![系统常量](https://renlm.cn/images/demo/200202.png "系统常量")
##### 资源管理
![资源管理](https://renlm.cn/images/demo/200203.png "资源管理")
#### 定时任务
##### 任务列表
![任务列表](https://renlm.cn/images/demo/200301.png "任务列表")
##### 任务日志
![任务日志](https://renlm.cn/images/demo/200302.png "任务日志")
### 文件管理
![文件管理](https://renlm.cn/images/demo/file.png "文件管理")
### 服务器监控
#### 风格一
![风格一](https://renlm.cn/images/demo/300101.png "风格一")
#### 风格二
![风格二](https://renlm.cn/images/demo/300102.png "风格二")
### 更多功能
![更多功能](https://renlm.cn/images/demo/400101.png "更多功能")

## 主要技术组件
SpringBoot + MyBatis-plus + My-Plugin  
MySQL + Redis + Rabbitmq  
Chrome + Chromedriver  
EasyUI + Mxgraph + Editor.md  

## 本地化部署
### 初始账号
```
超级管理员：S-linghc，密码：Aac^123654.
普通用户：S-renyy，密码：Aac^123654.
```
### 数据库初始脚本
数据库脚本在resource/shcema目录下，包含schema.sql（结构）和data.sql（初始数据）。  
项目默认启动test环境，启动dev环境会重建库。  
![数据库初始脚本](https://renlm.cn/images/demo/100.png "数据库初始脚本")
### Redis、Rabbitmq队列、数据库
本地化部署请修改Redis、Rabbitmq队列、数据库的地址账号及密码  
![redis、mq队列、数据库](https://renlm.cn/images/demo/101.png "redis、mq队列、数据库")
### 谷歌浏览器驱动
生成封面用到了谷歌浏览器驱动，请修改驱动地址  
![谷歌浏览器驱动](https://renlm.cn/images/demo/102.png "谷歌浏览器驱动")
### 修改自定义参数
配置文件中的自定义参数请修改为自己的项目地址  
服务器监控Websocket配置等需要  
![修改自定义参数](https://renlm.cn/images/demo/103.png "修改自定义参数")