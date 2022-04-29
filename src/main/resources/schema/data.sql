-- 用户（密码：S-crawler^3324.）
DELETE FROM sys_user;
INSERT INTO sys_user 
	   (id,		user_id, 							nickname, 	username,		password, 														mobile, 		email) 
VALUES (1,		'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲', 	'S-linghc',		'$2a$10$l90tWt3ZDJD95AmtdI3SHur.dm.M2HYLXMkZRSocIRzU0mLO/xTCK', '18801330084', 	'renlm@21cn.com'),
	   (2,		'AE82571C404544218D373B294EB87E80',	'任盈盈', 	'S-renyy',		'$2a$10$l90tWt3ZDJD95AmtdI3SHur.dm.M2HYLXMkZRSocIRzU0mLO/xTCK', '17338158607', 	'renlmer@qq.com')
;

-- 角色
DELETE FROM sys_role;
INSERT INTO sys_role 
	   (id,		role_id, 				code, 					text, 		level,	sort, 	state, 		pid)
VALUES (10,  	'1482644947575877632',	'PLATFORM',				'平台管理', 	1, 		1, 		'closed', 	null),
	   (1001,  	'1482645001363611648',	'SUPER', 				'超级管理员', 	2, 		1, 		'open', 	10),
	   (20,  	'1488419148647747584',	'CRAWLER',				'网络爬虫', 	1, 		2, 		'closed', 	null),
	   (2001,  	'1488419200896110592',	'CRAWLER-M', 			'爬虫管理员', 	2, 		1, 		'open', 	20)
;

-- 组织机构
DELETE FROM sys_org;
INSERT INTO sys_org
	   (id,		org_id, 				code, 						text, 				org_type_code,	leader_user_id, 					icon_cls, 	level, sort, state, 	pid)
VALUES (1,		'1482645888395915264',	'FIRE',						'火星科技股份有限公司',	'1',		  	null,								null,	    1,     1,    'closed',  null),
	   (10,		'1482646087877128192',	'FIRE-HRMC',				'人力资源管理中心',		'2',		  	'AE82571C404544218D373B294EB87E80',	null,	    2,     1,    'closed',  1),
	   (1001,	'1482646131992809472',	'FIRE-HRMC-ZZFZ',			'组织发展部',			'2',		  	null,								null,	    3,     1,    'open',  	10),
	   (20,		'1482646180038557696',	'FIRE-PRDMC',				'产品研发管理中心',		'2',		  	null,								null,	    2,     2,    'closed',  1),
	   (2001,	'1482646241363484672',	'FIRE-PRDMC-YF',			'研发管理部',			'2',		  	null,			  					null,	    3,     1,    'closed',  20),
	   (200101,	'1482646291644694528',	'FIRE-PRDMC-YF-QD',			'前端技术组',			'2',		  	null,			  					null,	    4,     1,    'open',  	2001),
	   (200102,	'1482646346506194944',	'FIRE-PRDMC-YF-JAVA',		'Java技术组',			'2',		  	null,								null,	    4,     2,    'open',  	2001),
	   (2002,	'1482646395470491648',	'FIRE-PRDMC-CP',			'产品管理部',			'2',		  	null,			  					null,	    3,     2,    'open',  	20),
	   (2003,	'1482646456061419520',	'FIRE-PRDMC-PUB',			'公共组',				'2',		  	null,			  					null,	    3,     3,    'open',  	20),
	   (30,		'1482646544687063040',	'FIRE-PTC',					'平台技术中心',			'2',		  	'ABFEE20BA6AC4783A683C7117763951E',	null,	    2,     3,    'closed',  1),
	   (3001,	'1482646593714368512',	'FIRE-PTC-JCFW',			'基础服务部',			'2',		  	null,			  					null,	    3,     1,    'open',  	30),
	   (3002,	'1482646672986632192',	'FIRE-PTC-YWJS',			'运维技术部',			'2',		  	null,			  					null,	    3,     2,    'open',  	30)
;

-- 用户角色关系
DELETE FROM sys_user_role;
INSERT INTO sys_user_role 
	   (sys_user_id,	sys_role_id)
VALUES (1,			 	1001),
	   (2,			 	2001)
;

-- 用户组织机构关系
DELETE FROM sys_user_org;
INSERT INTO sys_user_org
	   (sys_user_id, 	sys_org_id,	position_name)
VALUES (1,				30, 		'高级软件开发工程师'),
	   (2,				10, 		'高级企业人力资源管理师')
;

-- 资源
DELETE FROM sys_resource;
INSERT INTO sys_resource 
	   (id,				resource_id, 			code, 			text, 			resource_type_code, url, 														icon_cls, 					icon_cls_colour, 	text_colour, 	level, 	sort,	state,		commonly,	default_home_page,	pid,		remark)
VALUES (-1, 			'1487724509355577344',	'HOME', 		'主页', 			'permission', 		null, 														'fa fa-institution', 		null, 				null, 			1, 		0, 		'closed',	0,			0,					null,		null)
	  ,(-10, 			'1487725840367620096',	'WELCOME', 		'欢迎页', 		'permission', 		'/home/welcome', 											'fa fa-home', 				null, 				null, 			2, 		1, 		'open',		0,			1,					-1,			null)
	  ,(-20, 			'1487725892423245824',	'OSHI', 		'服务器监控', 		'permission', 		'/home/oshi', 												'fa fa-database', 			null, 				null, 			2, 		2, 		'open',		0,			0,					-1,			null)
	  ,(10, 			'1482653140498518016',	'10', 			'平台管理', 		'menu', 			null, 														'fa fa-cog', 				null, 				null, 			1, 		1, 		'closed',	0,			0,					null,		null)
	  ,(1001, 			'1482653221993951232',	'1001', 		'平台配置', 		'menu', 			null, 														'fa fa-windows', 			null, 				null, 			2, 		1, 		'closed',	0,			0,					10,			null)
	  ,(100101, 		'1482653267334279168',	'100101', 		'系统配置', 		'menu', 			null, 														'fa fa-cogs', 				null, 				null, 			3, 		1, 		'closed',	0,			0,					1001,		null)
	  ,(10010101, 		'1482653313886883840',	'10010101', 	'基础配置', 		'menu', 			'/sys/const', 												'fa fa-cube', 				null, 				null, 			4, 		1, 		'open',		0,			0,					100101,		null)
	  ,(10010102, 		'1482653361320271872',	'10010102', 	'资源管理', 		'menu', 			'/sys/resource', 											'fa fa-life-ring', 			null, 				null, 			4, 		2, 		'open',		0,			0,					100101,		null)
	  ,(10010103, 		'1482653408002846720',	'10010103', 	'数据字典', 		'menu', 			'/sys/dict', 												null, 						null, 				null, 			4, 		3, 		'open',		0,			0,					100101,		null)
	  ,(100102, 		'1482653513334415360',	'100102', 		'用户管理', 		'menu', 			null, 														'fa fa-user', 				null, 				null, 			3, 		2, 		'closed',	0,			0,					1001,		null)
	  ,(10010201, 		'1482653560457412608',	'10010201', 	'组织机构', 		'menu', 			'/sys/org', 												'fa fa-sitemap', 			null, 				null, 			4, 		1, 		'open',		0,			0,					100102,		null)
	  ,(10010202, 		'1482653609765752832',	'10010202', 	'用户信息', 		'menu', 			'/sys/user', 												'fa fa-user', 				null, 				null, 			4, 		2, 		'open',		0,			0,					100102,		null)
	  ,(10010203, 		'1482653656561602560',	'10010203', 	'角色权限', 		'menu', 			'/sys/role', 												'fa fa-user-circle', 		null, 				null, 			4, 		3, 		'open',		0,			0,					100102,		null)
	  ,(100103, 		'1482653705995571200',	'100103', 		'日志监控', 		'menu', 			null, 														'fa fa-heartbeat', 			null, 				null, 			3, 		3, 		'closed',	0,			0,					1001,		null)
	  ,(10010301, 		'1482653751512227840',	'10010301', 	'登录日志', 		'menu', 			'/log/login', 												'fa fa-file-text-o', 		null, 				null, 			4, 		1, 		'open',		0,			0,					100103,		null)
	  ,(1002, 			'1482653800052842496',	'1002', 		'任务调度', 		'menu', 			null, 														'fa fa-tasks', 				null, 				null, 			2, 		3, 		'closed',	0,			0,					10,			null)
	  ,(100201, 		'1482653856650768384',	'100201', 		'任务管理', 		'menu', 			'/qrtz/job', 												'fa fa-deaf', 				null, 				null, 			3, 		1, 		'open',		1,			0,					1002,		null)
	  ,(1003, 			'1482653903371227136',	'1003', 		'文件管理', 		'menu', 			null, 														'fa fa-book', 				null, 				null, 			2, 		4, 		'closed',	0,			0,					10,			null)
	  ,(100301, 		'1482653953463709696',	'100301', 		'文件列表', 		'menu', 			'/sys/file', 												null, 						null, 				null, 			3, 		1, 		'open',		1,			0,					1003,		null)
	  ,(20, 			'1482654004223270912',	'20', 			'网络爬虫', 		'menu',				null, 														'fa fa-bug', 				null, 				null, 			1, 		2, 		'closed',	0,			0,					null,		null)
	  ,(2001, 			'1482654051308527616',	'2001', 		'爬虫配置', 		'menu',				null, 														'fa fa-xing-square', 		null, 				null, 			2, 		1, 		'closed',	0,			0,					20,			null)
	  ,(200101, 		'1482654096401469440',	'200101', 		'公示列表', 		'menu',				'/spider/1481984066382835712', 								'fa fa-map-o', 				null, 				null, 			3, 		2, 		'open',		0,			0,					2001,		null)
	  ,(2002, 			'1482654142786195456',	'2002', 		'数据清单', 		'menu',				null, 														'fa fa-database', 			null, 				null, 			2, 		2, 		'closed',	0,			0,					20,			null)
	  ,(200201, 		'1482654188902547456',	'200201', 		'公示列表', 		'menu',				'/crawl/announce/1481984066382835712',						'fa fa-map-o', 				null, 				null, 			3, 		1, 		'open',		1,			0,					2002,		null)
	  ,(200202, 		'1486334338114535424',	'200202', 		'采购公告', 		'menu',				'/crawl/purchaseNotice/1481984066382835712',				'fa fa-bitcoin', 			null, 				null, 			3, 		2, 		'open',		0,			0,					2002,		null)
	  ,(200203, 		'1486334403935858688',	'200203', 		'采购结果公告', 	'menu',				'/crawl/purchaseResult/1481984066382835712',				'fa fa-random', 			null, 				null, 			3, 		3, 		'open',		0,			0,					2002,		null)
	  ,(200204, 		'1486334454837809152',	'200204', 		'招标公告', 		'menu',				'/crawl/bidNotice/1481984066382835712',						'fa fa-gg', 				null, 				null, 			3, 		4, 		'open',		0,			0,					2002,		null)
	  ,(200205, 		'1486334507077865472',	'200205', 		'中标候选人公示', 	'menu',				'/crawl/bidCandidate/1481984066382835712',					'fa fa-recycle', 			null, 				null, 			3, 		5, 		'open',		0,			0,					2002,		null)
	  ,(200206, 		'1486334586211799040',	'200206', 		'中标结果公告', 	'menu',				'/crawl/bidResult/1481984066382835712',						'fa fa-tv', 				null, 				null, 			3, 		6, 		'open',		0,			0,					2002,		null)
	  ,(30, 			'1489160595051237376',	'30', 			'图文识别', 		'menu',				null, 														'fa fa-low-vision', 		null, 				null, 			1, 		3, 		'closed',	0,			0,					null,		null)
	  ,(3001, 			'1489160653939232768',	'3001', 		'功能演示', 		'menu',				null, 														'fa fa-snowflake-o', 		null, 				null, 			2, 		1, 		'closed',	0,			0,					30,			null)
	  ,(300101, 		'1489160714068803584',	'300101', 		'DEMO', 		'menu',				'/ocr/demo', 												'fa fa-file-image-o', 		null, 				null, 			3, 		2, 		'open',		0,			0,					3001,		null)
	  ,(40, 			'1492413863286140928',	'40', 			'数据源', 		'menu',				null, 														'fa fa-soccer-ball-o', 		null, 				null, 			1, 		4, 		'closed',	0,			0,					null,		null)
	  ,(4001, 			'1492413931930075136',	'4001', 		'数据源', 		'menu',				null, 														'fa fa-server', 			null, 				null, 			2, 		1, 		'closed',	0,			0,					40,			null)
	  ,(400101, 		'1492413980500140032',	'400101', 		'源列表', 		'menu',				'/ds/list', 												'fa fa-etsy', 				null, 				null, 			3, 		1, 		'open',		0,			0,					4001,		null)
	  ,(50, 			'1492414079196311552',	'50', 			'图形设计', 		'menu',				null, 														'fa fa-audio-description',	null, 				null, 			1, 		5, 		'closed',	0,			0,					null,		null)
	  ,(5001, 			'1492414126713573376',	'5001', 		'图形设计', 		'menu',				null, 														'fa fa-server', 			null, 				null, 			2, 		1, 		'closed',	0,			0,					50,			null)
	  ,(500101, 		'1492414181017186304',	'500101', 		'设计图库', 		'menu',				'/mxgraph/lib', 											'fa fa-file-image-o', 		null, 				null, 			3, 		1, 		'open',		0,			0,					5001,		null)
	  ,(500102, 		'1492740643540705280',	'500102', 		'我的作品', 		'menu',				'/mxgraph/mine', 											'fa fa-user-plus', 			null, 				null, 			3, 		2, 		'open',		0,			0,					5001,		null)
	  ,(60, 			'1492740743725957120',	'60', 			'静态演示', 		'urlNewWindows',	'http://demo.topjui.com', 									'fa fa-sitemap', 			'#F7B824', 			null, 			1, 		6, 		'open',		0,			0,					null,		null)
;

-- 角色资源关系
DELETE FROM sys_role_resource;
INSERT INTO sys_role_resource(
	sys_role_id,	
	sys_resource_id,	
	alias,	
	sort,	
	commonly,	
	default_home_page,	
	hide
) SELECT 1001, -- sys_role_id		
	id,	-- sys_resource_id				
	CASE WHEN code = 'OSHI' then '服务器' else null end, -- alias
	sort, -- sort
	commonly, -- commonly
	CASE WHEN code = 'OSHI' then 1 else 0 end, -- default_home_page
	false -- hide
FROM sys_resource
;
INSERT INTO sys_role_resource(
	sys_role_id,
	sys_resource_id,	
	alias,	
	sort,	
	commonly,	
	default_home_page,	
	hide
) SELECT 2001, -- sys_role_id	
	id,	-- sys_resource_id				
	null, -- alias	
	sort, -- sort	
	commonly, -- commonly
	default_home_page, -- default_home_page
	CASE WHEN code = '10' then 1 else 0 end -- hide
FROM sys_resource
WHERE code IN ('HOME', 'WELCOME', '10', '1003', '100301', '20', '2001', '200101', '2002', '200201', '200202', '200203', '200204', '200205', '200206')
;

-- 系统常量
DELETE FROM sys_const;
INSERT INTO sys_const
	   (const_id,				code, 								name, 				val, 					sort, 	remark)
VALUES ('1482659697030336512',	'cfgSystemName',					'系统名称',			'Crawler 管理后台',		1,		'系统名称'),
	   ('1482659753204768768',	'cfgSystemVersion',					'系统版本',			'v1.0.1',				2,		'系统当前上线版本'),
	   ('1482659801116307456',	'cfgSystemVersionPublishDate',		'发布日期',			'2021-01-11',			3,		'系统当前上线版本的发布日期'),
	   ('1482659848067252224',	'cfgSiteBeian',						'网站备案号',			'京ICP备2021000671号',	4,		'网站上显示的备案号')
;

-- 图形设计
DELETE FROM graph;
INSERT INTO graph (
	uuid, 
	name, 
	category_code,
	category_name,
	is_public,
	created_at,
	creator_user_id,
	creator_nickname,
	updated_at
) VALUES (
	'198124BBCF284A40BB24CA315A7B8E36', 
	'演示DEMO',
	'ER',
	'ER模型',
	1,
	now(),
	'04AB1FE543D74862A1B35676C7BBE61B',
	'任黎明',
	now()
);