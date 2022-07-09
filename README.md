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
<a href="https://mygraph.renlm.cn/pub/doc/s/E42FB06DEC2545D6AC2947C5BF3DC750" target="_blank">系统手册</a>  

## 关键教程
### 在线文档
#### 新建文档项目
#### 成员管理
#### 编写文档

### 数据库设计

### 网关代理

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