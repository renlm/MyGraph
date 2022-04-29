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
	   (id,		role_id, 							code, 					text, 		level,	sort, 	state, 		pid)
VALUES (10,  	'F536D20F2ED04A5A9BA07EB4EFF1ABB4',	'PLATFORM',				'平台管理', 	1, 		1, 		'closed', 	null),
	   (1001,  	'286DCAA350354F1796D2B579D7D056E3',	'SUPER', 				'超级管理员', 	2, 		1, 		'open', 	10),
	   (20,  	'B8BA2320FBD945448B7192C461D209EF',	'COMMON',				'通用权限', 	1, 		2, 		'closed', 	null),
	   (2001,  	'D82CB5F509EE42578065B35699266FA3',	'GENERAL', 				'普通用户', 	2, 		1, 		'open', 	20),
	   (2002,  	'A50FEAE499214D1EB2CAC5074DC878DE',	'SELF', 				'自主注册', 	2, 		2, 		'open', 	20)
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
	   (id,				resource_id, 						code, 			text, 			resource_type_code, url, 														icon_cls, 					icon_cls_colour, 	text_colour, 	level, 	sort,	state,		commonly,	default_home_page,	pid,		remark)
VALUES (-1, 			'86D6BA0E2C764A6C8D6C6EB57898C4CC',	'HOME', 		'主页', 			'permission', 		null, 														'fa fa-institution', 		null, 				null, 			1, 		0, 		'closed',	0,			0,					null,		null)
	  ,(-10, 			'D81EE8F226A2463C99837F0B20CC3765',	'WELCOME', 		'欢迎页', 		'permission', 		'/home/welcome', 											'fa fa-home', 				null, 				null, 			2, 		1, 		'open',		0,			1,					-1,			null)
	  ,(-20, 			'5D745593F7BC493590F30E1DDCA21F9C',	'OSHI', 		'服务器监控', 		'permission', 		'/home/oshi', 												'fa fa-database', 			null, 				null, 			2, 		2, 		'open',		0,			0,					-1,			null)
	  ,(10, 			'FF784D818217460DB745C5013EE0C2B5',	'10', 			'工作台', 		'menu', 			null, 														'fa fa-windows', 			null, 				null, 			1, 		1, 		'closed',	0,			0,					null,		null)
	  ,(1001, 			'BFD547E1E8B241EF8B19E94B997FBBB6',	'1001', 		'图形设计', 		'menu', 			null, 														'fa fa-xing', 				null, 				null, 			2, 		1, 		'closed',	0,			0,					10,			null)
	  ,(100101, 		'1A2288C7B39D43D7B27CD7BD71A9EF44',	'100101', 		'我的作品', 		'menu', 			null, 														'fa fa-user-plus', 			null, 				null, 			3, 		1, 		'open',		0,			0,					1001,		null)
	  ,(1002, 			'89192E321F734C3DAA3093A4CF160872',	'1002', 		'数据源', 		'menu', 			null, 														'fa fa-soccer-ball-o', 		null, 				null, 			2, 		2, 		'closed',	0,			0,					10,			null)
	  ,(100201, 		'B7D338D9D1144D8E92A23F1D4B78E348',	'100201', 		'源列表', 		'menu', 			null, 														'fa fa-etsy', 				null, 				null, 			3, 		1, 		'open',		0,			0,					1002,		null)
	  ,(20, 			'35BD72793CF14E37947D9D0629B242BF',	'20', 			'系统管理', 		'menu', 			null, 														'fa fa-cog', 				null, 				null, 			1, 		2, 		'closed',	0,			0,					null,		null)
	  ,(30, 			'3165637A889443629EB021505A4CAC52',	'30', 			'服务器', 		'urlInsidePage', 	'/oshi', 													'fa fa-server', 			'#FF5722', 			null, 			1, 		3, 		'open',		0,			0,					null,		null)
	  ,(40, 			'FEA19FB294134CC19C74786577F21CBB',	'40', 			'公共图库', 		'urlInsidePage', 	'/graph/lib', 												'fa fa-audio-description', 	null, 				null, 			1, 		4, 		'open',		0,			0,					null,		null)
	  ,(50, 			'302F142FBA694822A32A9B38171DB851',	'50', 			'帮助中心', 		'urlInsidePage', 	'/static/editor.md/index.html', 							'fa fa-comments-o', 		null, 				null, 			1, 		5, 		'open',		0,			0,					null,		null)
	  ,(60, 			'396BDB80E54D4DC7A5AC63EF79D8A2F3',	'60', 			'更多功能', 		'more', 			null, 														'fa fa-th', 				null, 				null, 			1, 		6, 		'closed',	0,			0,					null,		null)
	  ,(6001, 			'991F18B5DEA54FCC99DFC6DC9AD6B22F',	'6001', 		'在线调试', 		'urlInsidePage', 	'/compile', 												'fa fa-code', 				null, 				null, 			2, 		1, 		'open',		0,			0,					60,			null)
	  ,(6002, 			'466FA46CA81B44A0B286294B6149007F',	'6002', 		'EasyUI中文站', 	'urlNewWindows', 	'http://www.jeasyui.cn/?from=demo', 						'fa fa-link', 				null, 				null, 			2, 		2, 		'open',		0,			0,					60,			null)
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
	false -- hide
FROM sys_resource
WHERE code IN ('HOME', 'WELCOME', '10', '1001', '100101', '1002', '100201', '40', '50', '60', '6001', '6002')
;
INSERT INTO sys_role_resource(
	sys_role_id,
	sys_resource_id,	
	alias,	
	sort,	
	commonly,	
	default_home_page,	
	hide
) SELECT 2002, -- sys_role_id	
	id,	-- sys_resource_id				
	null, -- alias	
	sort, -- sort	
	commonly, -- commonly
	default_home_page, -- default_home_page
	false -- hide
FROM sys_resource
WHERE code IN ('HOME', 'WELCOME', '10', '1001', '100101', '1002', '100201', '40', '50', '60', '6001', '6002')
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