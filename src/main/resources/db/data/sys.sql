-- 用户（密码：123654）
DELETE FROM sys_user;
INSERT INTO sys_user 
	   (id,		user_id, 							nickname, 	username,		password, 																mobile, 		email,				sign) 
VALUES (1,		'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲', 	'S-linghc',		'{bcrypt}$2a$10$n.2IUXpcfjjkcFpIXQRhH.uYl9d4Cre02U7aRS6B6S6ahF3lGxQnm', '18801330084', 	'renlm@21cn.com',	'在深邃的编码世界，做一枚轻盈的纸飞机'),
	   (2,		'AE82571C404544218D373B294EB87E80',	'任盈盈', 	'S-renyy',		'{bcrypt}$2a$10$n.2IUXpcfjjkcFpIXQRhH.uYl9d4Cre02U7aRS6B6S6ahF3lGxQnm', '17338158607', 	'renlmer@qq.com',	'凡人皆有一死')
;

-- 角色
DELETE FROM sys_role;
INSERT INTO sys_role 
	   (id,		role_id, 							code, 					text, 		level,	sort, 	state, 		pid)
VALUES (10,  	'F536D20F2ED04A5A9BA07EB4EFF1ABB4',	'PLATFORM',				'平台管理', 	1, 		1, 		'closed', 	null),
	   (1001,  	'286DCAA350354F1796D2B579D7D056E3',	'SUPER', 				'超级管理员', 	2, 		1, 		'open', 	10),
	   (20,  	'B8BA2320FBD945448B7192C461D209EF',	'COMMON',				'通用权限', 	1, 		2, 		'closed', 	null),
	   (2001,  	'D82CB5F509EE42578065B35699266FA3',	'GENERAL', 				'普通用户', 	2, 		1, 		'open', 	20)
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
	   (2,				10, 		'高级人力专员')
;

-- 资源
DELETE FROM sys_resource;
INSERT INTO sys_resource 
	   (id,				resource_id, 						code, 			text, 					resource_type_code, url, 														icon_cls, 					icon_cls_colour, 	text_colour, 	level, 	sort,	state,		default_home_page,	pid,		remark)
VALUES (1, 				'9AC40BC2E9A84761A6310A2BD6D4833A',	'WELCOME', 		'系统首页', 				'permission', 		'/home/welcome', 											'fa fa-home', 				null, 				null, 			1, 		0, 		'open',		0,					null,		null)
	  ,(10, 			'FF784D818217460DB745C5013EE0C2B5',	'10', 			'工作台', 				'menu', 			null, 														'fa fa-windows', 			null, 				null, 			1, 		1, 		'closed',	0,					null,		null)
	  ,(1001, 			'BFD547E1E8B241EF8B19E94B997FBBB6',	'1001', 		'在线画图', 				'menu', 			null, 														'fa fa-xing', 				null, 				null, 			2, 		1, 		'closed',	0,					10,			null)
	  ,(100101, 		'FEA19FB294134CC19C74786577F21CBB',	'100101', 		'图形设计', 				'urlInsidePage', 	'/graph/lib', 												'fa fa-image', 				null, 				null, 			3, 		1, 		'open',		0,					1001,		null)
	  ,(100102, 		'B7D338D9D1144D8E92A23F1D4B78E348',	'100102', 		'数据源', 				'menu', 			'/ds/list', 												'fa fa-soccer-ball-o', 		null, 				null, 			3, 		2, 		'open',		0,					1001,		null)
	  ,(1002, 			'9355B539E02A4BBBB0C8B3B0C3CF6F53',	'1002', 		'文档管理', 				'menu', 			null, 														'fa fa-file-word-o', 		null, 				null, 			2, 		2, 		'closed',	0,					10,			null)
	  ,(100201, 		'FB0C60FA256D4670B62A40D4EF13F59B',	'100201', 		'知识文库', 				'menu', 			'/doc/lib', 												'fa fa-vk', 				null, 				null, 			3, 		1, 		'open',		1,					1002,		null)
	  ,(20, 			'35BD72793CF14E37947D9D0629B242BF',	'20', 			'系统管理', 				'menu', 			null, 														'fa fa-cog', 				null, 				null, 			1, 		2, 		'closed',	0,					null,		null)
	  ,(2001, 			'F41159ACD09B4DD3B04D0E6EEAE6E875',	'2001', 		'权限管理', 				'menu', 			null, 														'fa fa-cube', 				null, 				null, 			2, 		1, 		'closed',	0,					20,			null)
	  ,(200101, 		'1A22415278094E2D802A73382B60CDA7',	'200101', 		'用户管理', 				'menu', 			'/sys/user', 												'fa fa-user', 				null, 				null, 			3, 		1, 		'open',		0,					2001,		null)
	  ,(200102, 		'27687B485B80460A80F077E964163D25',	'200102', 		'组织机构', 				'menu', 			'/sys/org', 												'fa fa-sitemap', 			null, 				null, 			3, 		2, 		'open',		0,					2001,		null)
	  ,(200103, 		'D182D4DA30F44B9A92CC57AC4A2A5918',	'200103', 		'角色管理', 				'menu', 			'/sys/role', 												'fa fa-user-circle', 		null, 				null, 			3, 		3, 		'open',		0,					2001,		null)
	  ,(200104, 		'650ED3D925B048738A9FC20A2B6DD6AE',	'200104', 		'登录日志', 				'menu', 			'/sys/loginLog', 											'fa fa-trello', 			null, 				null, 			3, 		4, 		'open',		0,					2001,		null)
	  ,(2002, 			'7D59CAD9CA0C4D69905612E2C801E87E',	'2002', 		'数据字典', 				'menu', 			null, 														'fa fa-cogs', 				null, 				null, 			2, 		2, 		'closed',	0,					20,			null)
	  ,(200201, 		'32DDAEA4491E4D03BE12A531FDA6D8A9',	'200201', 		'通用字典', 				'menu', 			'/sys/dict', 												'fa fa-calendar', 			null, 				null, 			3, 		1, 		'open',		0,					2002,		null)
	  ,(200202, 		'17A93E79D780487284BF886B99A05E2F',	'200202', 		'系统常量', 				'menu', 			'/sys/const', 												'fa fa-file-word-o', 		null, 				null, 			3, 		2, 		'open',		0,					2002,		null)
	  ,(200203, 		'E39F1F1E3D2245B98C349DBD791CDA1F',	'200203', 		'资源管理', 				'menu', 			'/sys/resource', 											'fa fa-css3', 				null, 				null, 			3, 		3, 		'open',		0,					2002,		null)
	  ,(2003, 			'BC5AC4EA07304BB59960E110767EE4C2',	'2003', 		'定时任务', 				'menu', 			null, 														'fa fa-tasks', 				null, 				null, 			2, 		3, 		'closed',	0,					20,			null)
	  ,(200301, 		'D957245380414774BF7882933536DA86',	'200301', 		'任务管理', 				'menu', 			'/qrtz/jobs', 												'fa fa-deaf', 				null, 				null, 			3, 		1, 		'open',		0,					2003,		null)
	  ,(30, 			'3165637A889443629EB021505A4CAC52',	'OSHI', 		'服务器', 				'permission', 		'/home/oshi',												'fa fa-server', 			null, 				null, 			1, 		3, 		'open',		1,					null,		null)
	  ,(40, 			'EB0B50741EAC47E3A275FEBCABFC5AA1',	'40', 			'文件管理', 				'urlInsidePage', 	'/sys/file', 												'fa fa-clipboard', 			'#990000', 			null, 			1, 		4, 		'open',		0,					null,		null)
	  ,(50, 			'731191740D22404585B359867121755B',	'50', 			'帮助文档', 				'menu', 			null, 														'fa fa-file-word-o', 		null, 				null, 			1, 		5, 		'closed',	0,					null,		null)
	  ,(5001, 			'D654AA8332BD40DC8C3DBF27F58F8B2F',	'5001', 		'使用手册', 				'menu', 			null, 														'fa fa-text-width', 		null, 				null, 			2, 		1, 		'closed',	0,					50,			null)
	  ,(500101, 		'CF11DB769EDA48C39C684AC866E18105',	'500101', 		'系统简介', 				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			3, 		1, 		'open',		0,					5001,		null)
	  ,(500102, 		'35111CB38E564A2684B934D954BA6F0F',	'500102', 		'Markdown', 			'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			3, 		2, 		'open',		0,					5001,		null)
	  ,(500103, 		'C5A770EB80584C919E106F2FC9358AE4',	'500103', 		'开放能力', 				'menu', 			null, 														'tree-icon tree-folder', 	null, 				null, 			3, 		3, 		'closed',	0,					5001,		null)
	  ,(50010301, 		'946DDABD8C6040DFB3910D8A9182E8D3',	'50010301', 	'网关代理', 				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			4, 		1, 		'open',		0,					500103,		null)
	  ,(500104, 		'92263CD5C7274FB1999DB8770A3D75A9',	'500104', 		'开放接口', 				'menu', 			null, 														'tree-icon tree-folder', 	null, 				null, 			3, 		4, 		'closed',	0,					5001,		null)
	  ,(50010401, 		'38AB9E6321994EA6A1F8A31AB49AC266',	'50010401', 	'数据字典',				'menu', 			null, 														'tree-icon tree-folder', 	null, 				null, 			4, 		1, 		'closed',	0,					500104,		null)
	  ,(5001040101, 	'954755B97FE84578A268CDBAEA0C8096',	'5001040101', 	'获取子集列表',				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		1, 		'open',		0,					50010401,	null)
	  ,(5001040102, 	'9227C84C5A8E4260AAA43F444BC2FA49',	'5001040102', 	'获取下级列表',				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		2, 		'open',		0,					50010401,	null)
	  ,(5001040103, 	'50F13E50709A47579B3301FB2D76B1C6',	'5001040103', 	'获取树形字典',				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		3, 		'open',		0,					50010401,	null)
	  ,(50010402, 		'1C333FE5F18043F49F3AE6B0DAF21965',	'50010402', 	'单点登录',				'menu', 			null, 														'tree-icon tree-folder', 	null, 				null, 			4, 		2, 		'closed',	0,					500104,		null)
	  ,(5001040201, 	'C212F7456C694035A3D0D89D1234377F',	'5001040201', 	'验证码图片',				'menu', 			null, 														'tree-icon tree-folder', 	null, 				null, 			5, 		1, 		'closed',	0,					50010402,	null)
	  ,(500104020101, 	'AF0BC33AACFC469D96CE079D983F8175',	'500104020101', '默认风格',				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			6, 		1, 		'open',		0,					5001040201,	null)
	  ,(5001040202, 	'A8133CDDFA7E4FDE9A0E005E589EA560',	'5001040202', 	'获取会话秘钥',				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		2, 		'open',		0,					50010402,	null)
	  ,(5001040203, 	'73CBEA8B2F1E45A39538453B7FBB9F96',	'5001040203', 	'单点登录页',				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		3, 		'open',		0,					50010402,	null)
	  ,(5001040204, 	'4579B97F59ED4A0FBDAE73BE7C72CADD',	'5001040204', 	'登录接口',				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		4, 		'open',		0,					50010402,	null)
	  ,(5001040205, 	'700CB22316704796BA3B421C0D96D38A',	'5001040205', 	'退出登录',				'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		5, 		'open',		0,					50010402,	null)
	  ,(5001040206, 	'49866E08C4A5415CBFEAD99647E0F4BE',	'5001040206', 	'获取当前登录用户',			'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		6, 		'open',		0,					50010402,	null)
	  ,(5001040207, 	'268578A10DB944989D277518466F5F74',	'5001040207', 	'根据Ticket查询用户', 		'markdown', 		null, 														'tree-icon tree-file', 		null, 				null, 			5, 		7, 		'open',		0,					50010402,	null)
	  ,(60, 			'396BDB80E54D4DC7A5AC63EF79D8A2F3',	'60', 			'更多功能', 				'more', 			null, 														'fa fa-th', 				null, 				null, 			1, 		6, 		'closed',	0,					null,		null)
	  ,(6001, 			'991F18B5DEA54FCC99DFC6DC9AD6B22F',	'6001', 		'在线调试', 				'urlInsidePage', 	'/compile', 												'fa fa-code', 				'rebeccapurple', 	null, 			2, 		1, 		'open',		0,					60,			null)
	  ,(6002, 			'466FA46CA81B44A0B286294B6149007F',	'6002', 		'Editor.md', 			'urlNewWindows', 	'/static/markdown/editor.md-1.5.0/examples/index.html', 	'fa fa-file-word-o', 		null, 				null, 			2, 		2, 		'open',		0,					60,			null)
	  ,(6003, 			'674BB526D3BE4231A81C2311E5E14AB0',	'6003', 		'EasyUI中文站', 			'urlNewWindows', 	'http://www.jeasyui.cn/?from=demo', 						'fa fa-link', 				null, 				null, 			2, 		3, 		'open',		0,					60,			null)
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
WHERE code IN ('WELCOME', '10', '1001', '100101', '100102', '1002', '100201', '40')
	OR 
	code LIKE '50%'
;

-- 系统常量
DELETE FROM sys_const;
INSERT INTO sys_const
	   (const_id,							code, 								name, 				val, 					sort, 	remark)
VALUES ('BA8A2BE8EFE24768942B7BA468CD3440',	'cfgSystemName',					'系统名称',			'MyGraph 管理后台',		1,		'系统名称'),
	   ('1E358EC8EB6F4C9391EF59AC99326ABB',	'cfgSystemVersion',					'系统版本',			'v1.0.3',				2,		'系统当前上线版本'),
	   ('1199F98DB51143B7B4825ADB91759E56',	'cfgSystemVersionPublishDate',		'发布日期',			'2022-07-21',			3,		'系统当前上线版本的发布日期'),
	   ('828E0187483F4F768571C1A749DD63EF',	'cfgSiteBeian',						'网站备案号',			'京ICP备2021000671号',	4,		'网站上显示的备案号')
;
