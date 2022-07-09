## 简介
### 这是一个权限管理系统
实现了完整的用户、组织机构、角色资源、数据字典、定时任务、文件管理、登录日志等功能。  

### 这是一个独立的用户中心
提供单点登录、统一会话及前后端分离跨域的解决方案，开放接口能力，提供通用数据字典接口。  

### 这是一个动态实时配置的网关代理
提供了在线配置、限速、日志与流量统计等功能。  

### 这是一个在线文档系统
集成语法增强版的Markdown文档、在线画图、数据库ER模型设计、在线数据表格，支持团队协作、版本控制与分享。  

## 系统体验
体验地址：<a href="https://mygraph.renlm.cn/" target="_blank">https://<span></span>mygraph.renlm.cn/</a>  
***此为体验系统，请不要修改密码，每天晚上定时重置为初始状态。***  
```
账号（超级管理员）：S-linghc  
账号（普通用户）：S-renyy  
统一密码：123654  
```

## 关键教程
### 在线文档
#### 新建项目
![新建项目](https://renlm.cn/images/demo/201.png "新建项目")

#### 成员管理
![成员管理](https://renlm.cn/images/demo/202.png "成员管理")

#### 新建文档
支持多级目录，支持Markdown文档、图形设计、在线表格三种类型。
![点击项目名称，进入文档管理](https://renlm.cn/images/demo/210.png "点击项目名称，进入文档管理")
![新建分类](https://renlm.cn/images/demo/211.png "新建分类")
![新建子级](https://renlm.cn/images/demo/212.png "新建子级")
![编辑文档](https://renlm.cn/images/demo/213.png "编辑文档")
![Markdown文档](https://renlm.cn/images/demo/214.png "Markdown文档")

### 分享与收藏
收藏或分享后，在知识文库页面可快速进入。
![分享与收藏](https://renlm.cn/images/demo/215.png "分享与收藏")

### 数据库设计
#### 从数据源生成ER图

#### 从零开始设计数据库

#### 导出DDL

### 网关代理
#### 新建代理配置

#### 验证配置、日志记录及统计数据是否正常

### 单点登录集成
#### 统一登录及用户中心

#### 公共字典

#### 集成前后端分离及统一会话

## 主要技术组件
SpringBoot2.5.3 + SpringSecurity + MyBatis-plus + My-Plugin  
MySQL + Redis + Rabbitmq  
Chrome + Chromedriver  
EasyUI + Mxgraph + Editor.md  

## 本地化部署
### 初始账号
```
超级管理员：S-linghc，密码：123654
普通用户：S-renyy，密码：123654
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