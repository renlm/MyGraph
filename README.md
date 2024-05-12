## 简介
### 这是一个经典RABC权限后台
实现了完整的用户、组织机构、角色资源、数据字典、定时任务、文件管理、登录日志等功能。  

### 这是一个Oauth2.0认证服务
支持授权码、客户端及自定义的密码模式。  

### 这是一个在线画图工具
强化支持ER模型设计，可从数据库直接逆向生成ER图，并可由ER图导出DDL建表语句。  
兼容多种数据库，目前已实现对MySQL、PostgreSQL的支持，可自由扩展。  
自动计算图形尺寸，并生成封面图。  

### 这是一个在线文档系统
集成语法增强版的Markdown文档，支持团队协作、版本控制与分享。  
分享文档：<a href="https://mygraph.renlm.cn/pub/doc/s/F6518D2F9B4542D1B8AD48325066A20B" target="_blank"> 系统手册 </a>，查看密码：123654  

## 源码地址 
<a href="https://gitee.com/renlm/MyGraph.git" target="_blank">https://<span></span>gitee.com/renlm/MyGraph.git</a>  
<a href="https://github.com/renlm/MyGraph.git" target="_blank">https://<span></span>github.com/renlm/MyGraph.git</a>  

## 系统体验
体验地址：<a href="https://mygraph.renlm.cn/" target="_blank">https://<span></span>mygraph.renlm.cn/</a>  
***此为体验系统，请不要修改密码，不定期重置为初始状态。***  
```
账号（超级管理员）：S-linghc  
账号（普通用户）：S-renyy  
统一密码：123654  
```

## 关键教程
### 在线文档
#### 新建项目

![新建项目](https://renlm.github.io/imgs/MyGraph/201.png "新建项目")

#### 成员管理

![成员管理](https://renlm.github.io/imgs/MyGraph/202.png "成员管理")

#### 新建文档

支持多级目录，支持Markdown文档、图形设计、在线表格三种类型。

![点击项目名称，进入文档管理](https://renlm.github.io/imgs/MyGraph/210.png "点击项目名称，进入文档管理")

![新建分类](https://renlm.github.io/imgs/MyGraph/211.png "新建分类")

![新建子级](https://renlm.github.io/imgs/MyGraph/212.png "新建子级")

![编辑文档](https://renlm.github.io/imgs/MyGraph/213.png "编辑文档")

![Markdown文档](https://renlm.github.io/imgs/MyGraph/214.png "Markdown文档")

#### 分享与收藏
收藏或分享后，在知识文库可快速进入。

![分享与收藏](https://renlm.github.io/imgs/MyGraph/215.png "分享与收藏")

### 数据库设计
#### 从数据源生成ER图
新建数据源，选择表生成ER图后，根据提示进入文档管理中查看编辑图形。

![新建数据源](https://renlm.github.io/imgs/MyGraph/301.png "新建数据源")

![选择表](https://renlm.github.io/imgs/MyGraph/302.png "选择表")

![生成ER图](https://renlm.github.io/imgs/MyGraph/303.png "生成ER图")

#### 数据库ER模型设计

![打开工具](https://renlm.github.io/imgs/MyGraph/304.png "打开工具")

![工具栏](https://renlm.github.io/imgs/MyGraph/305.png "工具栏")

![添加表](https://renlm.github.io/imgs/MyGraph/306.png "添加表")

![添加表](https://renlm.github.io/imgs/MyGraph/307.png "添加表")

![双击表编辑](https://renlm.github.io/imgs/MyGraph/308.png "双击表编辑")

![我的字段库](https://renlm.github.io/imgs/MyGraph/309.png "我的字段库")

#### 导出DDL
图形分类为ER模型的才可导出DDL，下载的DDL建表语句带注释，可直接执行。

![下载DDL](https://renlm.github.io/imgs/MyGraph/310.png "下载DDL")

![DDL](https://renlm.github.io/imgs/MyGraph/311.png "DDL")

#### 图形设计
展示所有公开及自己名下的图。  
除了数据库ER图，同时支持其它诸如流程图、UML、思维导图等常见图形。  
自动计算图形尺寸并生成封面。  

![所有公开及自己名下的图](https://renlm.github.io/imgs/MyGraph/312.png "所有公开及自己名下的图")

## 主要技术组件
代码仓库：gitee、github  
Docker容器镜像仓库：阿里云个人免费版  
私有Chart仓库、自动化脚本、笔记：github 静态托管  
资源：京东云服务器（ubuntu）、域名（renlm.cn）、公网IP、cert-manager自申请免费https证书  
容器化部署流水线：k3s+rancher、k8s、Jenkins  
服务监控： Monitoring（Grafana + Prometheus）  
网关、流量及链路追踪：Istio（Kiali + Jaeger）  
微服务架构：最新版 SpringCloud2023.0.1（Service Mesh）  
辅助开发调试工具：VSCode、Nocalhost插件  

## 初始化部署
<a href="https://renlm.github.io" target="_blank">配置手册</a>  

部署清单：devops/fleet  

![Kiali Console](https://renlm.github.io/imgs/MyGraph/100.png "Kiali Console")  
