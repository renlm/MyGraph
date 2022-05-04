-- 用户（密码：S-crawler^3324.）
DELETE FROM sys_user;
INSERT INTO sys_user 
	   (id,		user_id, 							nickname, 	username,		password, 														mobile, 		email,				sign) 
VALUES (1,		'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲', 	'S-linghc',		'$2a$10$l90tWt3ZDJD95AmtdI3SHur.dm.M2HYLXMkZRSocIRzU0mLO/xTCK', '18801330084', 	'renlm@21cn.com',	'在深邃的编码世界，做一枚轻盈的纸飞机'),
	   (2,		'AE82571C404544218D373B294EB87E80',	'任盈盈', 	'S-renyy',		'$2a$10$l90tWt3ZDJD95AmtdI3SHur.dm.M2HYLXMkZRSocIRzU0mLO/xTCK', '17338158607', 	'renlmer@qq.com',	'凡人皆有一死')
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
	   (id,		org_id, 							code, 						text, 				org_type_code,	leader_user_id, 					icon_cls, 	level, sort, state, 	pid)
VALUES (1,		'B89E08CA5C0D4C26AAEE66AD7DD05F32',	'FIRE',						'火星科技股份有限公司',	'1',		  	null,								null,	    1,     1,    'closed',  null),
	   (10,		'166E7AC4D0EA4DF0BDFC3494A94C0E00',	'FIRE-HRMC',				'人力资源管理中心',		'2',		  	'AE82571C404544218D373B294EB87E80',	null,	    2,     1,    'closed',  1),
	   (1001,	'538C7D2A27C2409DB79EF1FDD55ED4FA',	'FIRE-HRMC-ZZFZ',			'组织发展部',			'2',		  	null,								null,	    3,     1,    'open',  	10),
	   (20,		'F8D36B22B9ED4F91AF8137AE8598E72B',	'FIRE-PRDMC',				'产品研发管理中心',		'2',		  	null,								null,	    2,     2,    'closed',  1),
	   (2001,	'397F48F5EC364453933170EF4389F3F2',	'FIRE-PRDMC-YF',			'研发管理部',			'2',		  	null,			  					null,	    3,     1,    'closed',  20),
	   (200101,	'0439F0A26FA040F0B69C744C6141FA60',	'FIRE-PRDMC-YF-QD',			'前端技术组',			'2',		  	null,			  					null,	    4,     1,    'open',  	2001),
	   (200102,	'34605729040D4D4CA86B2E766DA3F888',	'FIRE-PRDMC-YF-JAVA',		'Java技术组',			'2',		  	null,								null,	    4,     2,    'open',  	2001),
	   (2002,	'1BB92E1E12B944708FFCC9DE543A4F14',	'FIRE-PRDMC-CP',			'产品管理部',			'2',		  	null,			  					null,	    3,     2,    'open',  	20),
	   (2003,	'F6E7E097264C4740AB5BF008777C601E',	'FIRE-PRDMC-PUB',			'公共组',				'2',		  	null,			  					null,	    3,     3,    'open',  	20),
	   (30,		'E9037686D3B34F1FB7498F58A245110E',	'FIRE-PTC',					'平台技术中心',			'2',		  	'ABFEE20BA6AC4783A683C7117763951E',	null,	    2,     3,    'closed',  1),
	   (3001,	'093A05A3597D453B808B16C1438EF9CE',	'FIRE-PTC-JCFW',			'基础服务部',			'2',		  	null,			  					null,	    3,     1,    'open',  	30),
	   (3002,	'D09AC370D0424A26BF0DE2C97CAB9AD5',	'FIRE-PTC-YWJS',			'运维技术部',			'2',		  	null,			  					null,	    3,     2,    'open',  	30)
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
	   (id,				resource_id, 						code, 			text, 			resource_type_code, url, 														icon_cls, 					icon_cls_colour, 	text_colour, 	level, 	sort,	state,		default_home_page,	pid,		remark)
VALUES (1, 				'9AC40BC2E9A84761A6310A2BD6D4833A',	'WELCOME', 		'系统首页', 		'permission', 		'/home/welcome', 											'fa fa-home', 				null, 				null, 			1, 		1, 		'open',		1,					null,		null)
	  ,(10, 			'FF784D818217460DB745C5013EE0C2B5',	'10', 			'工作台', 		'menu', 			null, 														'fa fa-windows', 			null, 				null, 			1, 		1, 		'closed',	0,					null,		null)
	  ,(1001, 			'BFD547E1E8B241EF8B19E94B997FBBB6',	'1001', 		'图形设计', 		'menu', 			null, 														'fa fa-xing', 				null, 				null, 			2, 		1, 		'closed',	0,					10,			null)
	  ,(100101, 		'1A2288C7B39D43D7B27CD7BD71A9EF44',	'100101', 		'我的', 			'menu', 			'/graph/mine', 												'fa fa-user-plus', 			null, 				null, 			3, 		1, 		'open',		0,					1001,		null)
	  ,(100102, 		'FEA19FB294134CC19C74786577F21CBB',	'100102', 		'公共图库', 		'urlInsidePage', 	'/graph/lib', 												'fa fa-image', 				null, 				null, 			3, 		2, 		'open',		0,					1001,		null)
	  ,(100103, 		'B7D338D9D1144D8E92A23F1D4B78E348',	'100103', 		'数据源', 		'menu', 			'/ds/list', 												'fa fa-soccer-ball-o', 		null, 				null, 			3, 		3, 		'open',		0,					1001,		null)
	  ,(20, 			'35BD72793CF14E37947D9D0629B242BF',	'20', 			'系统管理', 		'menu', 			null, 														'fa fa-cog', 				null, 				null, 			1, 		2, 		'closed',	0,					null,		null)
	  ,(2001, 			'F41159ACD09B4DD3B04D0E6EEAE6E875',	'2001', 		'权限管理', 		'menu', 			null, 														'fa fa-cube', 				null, 				null, 			2, 		1, 		'closed',	0,					20,			null)
	  ,(200101, 		'1A22415278094E2D802A73382B60CDA7',	'200101', 		'用户管理', 		'menu', 			null, 														'fa fa-user', 				null, 				null, 			3, 		1, 		'open',		0,					2001,		null)
	  ,(200102, 		'27687B485B80460A80F077E964163D25',	'200102', 		'组织机构', 		'menu', 			null, 														'fa fa-sitemap', 			null, 				null, 			3, 		2, 		'open',		0,					2001,		null)
	  ,(200103, 		'D182D4DA30F44B9A92CC57AC4A2A5918',	'200103', 		'角色管理', 		'menu', 			null, 														'fa fa-user-circle', 		null, 				null, 			3, 		3, 		'open',		0,					2001,		null)
	  ,(200104, 		'E39F1F1E3D2245B98C349DBD791CDA1F',	'200104', 		'资源列表', 		'menu', 			null, 														'fa fa-life-ring', 			null, 				null, 			3, 		4, 		'open',		0,					2001,		null)
	  ,(2002, 			'7D59CAD9CA0C4D69905612E2C801E87E',	'2002', 		'数据字典', 		'menu', 			null, 														'fa fa-cogs', 				null, 				null, 			2, 		2, 		'closed',	0,					20,			null)
	  ,(200201, 		'32DDAEA4491E4D03BE12A531FDA6D8A9',	'200201', 		'系统常量', 		'menu', 			'/sys/const', 												'fa fa-file-word-o', 		null, 				null, 			3, 		1, 		'open',		0,					2002,		null)
	  ,(200202, 		'17A93E79D780487284BF886B99A05E2F',	'200202', 		'通用字典', 		'menu', 			'/sys/dict', 												'fa fa-calendar', 			null, 				null, 			3, 		2, 		'open',		0,					2002,		null)
	  ,(2003, 			'BC5AC4EA07304BB59960E110767EE4C2',	'2003', 		'定时任务', 		'menu', 			null, 														'fa fa-tasks', 				null, 				null, 			2, 		3, 		'closed',	0,					20,			null)
	  ,(200301, 		'D957245380414774BF7882933536DA86',	'200301', 		'任务管理', 		'menu', 			null, 														'fa fa-deaf', 				null, 				null, 			3, 		1, 		'open',		0,					2003,		null)
	  ,(30, 			'3165637A889443629EB021505A4CAC52',	'OSHI', 		'服务器', 		'urlInsidePage', 	'/home/oshi',												'fa fa-server', 			'#FF5722', 			null, 			1, 		3, 		'open',		0,					null,		null)
	  ,(40, 			'EB0B50741EAC47E3A275FEBCABFC5AA1',	'40', 			'文件管理', 		'urlInsidePage', 	'/sys/file', 												'fa fa-clipboard', 			null, 				null, 			1, 		4, 		'open',		0,					null,		null)
	  ,(50, 			'396BDB80E54D4DC7A5AC63EF79D8A2F3',	'50', 			'更多功能', 		'more', 			null, 														'fa fa-th', 				null, 				null, 			1, 		5, 		'closed',	0,					null,		null)
	  ,(5001, 			'991F18B5DEA54FCC99DFC6DC9AD6B22F',	'5001', 		'在线调试', 		'urlInsidePage', 	'/compile', 												'fa fa-code', 				'rebeccapurple', 	null, 			2, 		1, 		'open',		0,					50,			null)
	  ,(5002, 			'466FA46CA81B44A0B286294B6149007F',	'5002', 		'EasyUI中文站', 	'urlNewWindows', 	'http://www.jeasyui.cn/?from=demo', 						'fa fa-link', 				null, 				null, 			2, 		2, 		'open',		0,					50,			null)
;

-- 角色资源关系
DELETE FROM sys_role_resource;
INSERT INTO sys_role_resource(
	sys_role_id,	
	sys_resource_id,	
	alias,	
	sort,	
	default_home_page
) SELECT 1001, -- sys_role_id		
	id,	-- sys_resource_id				
	null, -- alias	
	sort, -- sort
	default_home_page -- default_home_page
FROM sys_resource
;
INSERT INTO sys_role_resource(
	sys_role_id,
	sys_resource_id,	
	alias,	
	sort,	
	default_home_page
) SELECT 2001, -- sys_role_id	
	id,	-- sys_resource_id				
	null, -- alias	
	sort, -- sort	
	default_home_page -- default_home_page
FROM sys_resource
WHERE code IN ('WELCOME', '10', '1001', '100101', '100102', '100103', '40')
;
INSERT INTO sys_role_resource(
	sys_role_id,
	sys_resource_id,	
	alias,	
	sort,	
	default_home_page
) SELECT 2002, -- sys_role_id	
	id,	-- sys_resource_id				
	null, -- alias	
	sort, -- sort	
	default_home_page -- default_home_page
FROM sys_resource
WHERE code IN ('WELCOME', '10', '1001', '100101', '100102', '100103', '40')
;

-- 系统常量
DELETE FROM sys_const;
INSERT INTO sys_const
	   (const_id,							code, 								name, 				val, 					sort, 	remark)
VALUES ('BA8A2BE8EFE24768942B7BA468CD3440',	'cfgSystemName',					'系统名称',			'MyGraph 在线画图',		1,		'系统名称'),
	   ('1E358EC8EB6F4C9391EF59AC99326ABB',	'cfgSystemVersion',					'系统版本',			'v1.0.1',				2,		'系统当前上线版本'),
	   ('1199F98DB51143B7B4825ADB91759E56',	'cfgSystemVersionPublishDate',		'发布日期',			'2022-04-29',			3,		'系统当前上线版本的发布日期'),
	   ('828E0187483F4F768571C1A749DD63EF',	'cfgSiteBeian',						'网站备案号',			'京ICP备2021000671号',	4,		'网站上显示的备案号'),
	   ('FE4640997D1648FD96835A414B4D6F37',	'cfgEnableRegistration',			'是否启用注册账号',		'否',					5,		'是/否')
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
	'ABFEE20BA6AC4783A683C7117763951E',
	'任黎明',
	now()
);