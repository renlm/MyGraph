# 简介
ER模型在线设计与管理，整合一套后台模板，支持Markdown文档在线管理与服务器实时监控。  
免费开源（木兰宽松许可证, 第2版）  
ER图例（双击表可查看字段）：___<a href="https://mygraph.renlm.cn/graph/viewer?uuid=198124BBCF284A40BB24CA315A7B8E36&headless=false&fitWindow=false" target="_blank">MyGraph 在线画图</a>___  
Markdown扩展：___<a href="https://mygraph.renlm.cn/static/markdown/editor.md-1.5.0/examples/custom-extras.html?fullscreen=true" target="_blank"> 在线编辑器 </a>___ ， ___<a href="https://mygraph.renlm.cn/static/markdown/editor.md-1.5.0/examples/index.html" target="_blank"> 完整示例 </a>___

# 系统体验
地址：<a href="https://mygraph.renlm.cn/" target="_blank">https://mygraph.renlm.cn/</a>  
账号：S-renyy  
密码（每5分钟重置一次）：Aac^123654.  
项目部署相关请在体验系统查看详细文档

# 功能特色
1、支持从数据库生成ER图，新建、删除表，自由拖动连线，增、删、改、查、拖动排序字段，简洁操作加必要校验  
2、自动计算图形尺寸，生成封面图片  
3、用户管理、组织机构、角色管理（功能权限、授权人员、自定义）、登录日志  
4、通用字典、系统常量、资源管理  
5、定时任务  
6、文件管理  
7、服务器实时监控（两种风格）  
8、Markdown文档在线编辑管理，并扩展了Markdown语法与工具（Json文档、Layui皮肤、Echarts图表），丰富展现形式。  

# 功能截图
> ___图形设计-我的___

![图形设计-我的](https://renlm.cn/images/demo/1.png "图形设计-我的")

> ___图形设计-公共图库___

![图形设计-公共图库](https://renlm.cn/images/demo/2.png "图形设计-公共图库")

> ___图形设计-数据源___

![图形设计-数据源](https://renlm.cn/images/demo/3.png "图形设计-数据源")

> ___编辑ER图（Ctrl + S 保存）___

![编辑ER图（Ctrl + S 保存）](https://renlm.cn/images/demo/4.png "编辑ER图（Ctrl + S 保存）")

> ___编辑表___

![编辑表](https://renlm.cn/images/demo/5.png "编辑表")

> ___选择表___

![选择表](https://renlm.cn/images/demo/6.png "选择表")

> ___我的字段库___

![我的字段库](https://renlm.cn/images/demo/7.png "我的字段库")

> ___系统管理-权限管理-用户管理___

![系统管理-权限管理-用户管理](https://renlm.cn/images/demo/8.png "系统管理-权限管理-用户管理")

> ___系统管理-权限管理-组织机构___

![系统管理-权限管理-组织机构](https://renlm.cn/images/demo/9.png "系统管理-权限管理-组织机构")

> ___系统管理-权限管理-角色管理___

![系统管理-权限管理-角色管理](https://renlm.cn/images/demo/10.png "系统管理-权限管理-角色管理")

> ___系统管理-权限管理-登录日志___

![系统管理-权限管理-登录日志](https://renlm.cn/images/demo/11.png "系统管理-权限管理-登录日志")

> ___系统管理-数据字典-通用字典___

![系统管理-数据字典-通用字典](https://renlm.cn/images/demo/12.png "系统管理-数据字典-通用字典")

> ___系统管理-数据字典-系统常量___

![系统管理-数据字典-系统常量](https://renlm.cn/images/demo/13.png "系统管理-数据字典-系统常量")

> ___系统管理-数据字典-资源管理___

![系统管理-数据字典-资源管理](https://renlm.cn/images/demo/14.png "系统管理-数据字典-资源管理")

> ___系统管理-数据字典-文档编辑___

![系统管理-数据字典-文档编辑](https://renlm.cn/images/demo/15.png "系统管理-数据字典-文档编辑")

> ___系统管理-定时任务-任务管理___

![系统管理-定时任务-任务管理](https://renlm.cn/images/demo/16.png "系统管理-定时任务-任务管理")

> ___服务器实时监控-风格1___

![服务器实时监控-风格1](https://renlm.cn/images/demo/17.png "服务器实时监控-风格1")

> ___服务器实时监控-风格2___

![服务器实时监控-风格2](https://renlm.cn/images/demo/18.png "服务器实时监控-风格2")

> ___文件管理___

![文件管理](https://renlm.cn/images/demo/19.png "文件管理")

> ___帮助文档___

![帮助文档](https://renlm.cn/images/demo/20.png "帮助文档")

> ___更多功能___

![更多功能](https://renlm.cn/images/demo/21.png "更多功能")

# 主要技术组件
SpringBoot + MyBatis-plus + MySQL  
EasyUI + Mxgraph

# 本地化部署
1、数据库脚本在resource/shcema目录下，包含schema.sql（结构）和data.sql（初始数据），项目默认启动test环境，启动dev环境会重建库。
```
<1>.初始账号（超级管理员）：S-linghc，密码：Aac^123654.
<2>.初始账号（普通用户）：S-renyy，密码：Aac^123654.
```
2、本地化部署请修改redis、mq队列、数据库的地址账号及密码  
3、生成封面用到了谷歌浏览器驱动，请修改驱动地址  
4、配置文件中的自定义参数请修改为自己的项目地址  

> ___数据库初始脚本___

![数据库初始脚本](https://renlm.cn/images/demo/100.png "数据库初始脚本")

> ___redis、mq队列、数据库___

![redis、mq队列、数据库](https://renlm.cn/images/demo/102.png "redis、mq队列、数据库")

> ___谷歌浏览器驱动___

![谷歌浏览器驱动](https://renlm.cn/images/demo/103.png "谷歌浏览器驱动")

> ___修改自定义参数（服务器监控websocket配置等需要）___

![修改自定义参数](https://renlm.cn/images/demo/101.png "修改自定义参数")