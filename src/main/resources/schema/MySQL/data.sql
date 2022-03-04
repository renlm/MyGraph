-- 用户
DELETE FROM user;
INSERT INTO user (
	user_id, 
	username, 
	password, 
	nickname, 
	mobile, 
	email, 
	role,
	enabled
) VALUES (
	'04AB1FE543D74862A1B35676C7BBE61B', 
	'renlm', 
	'$2a$10$QxpfaI/kShOiZ50JQf2tTO5rK6I9tW2ig0AScKfmWB//M3CdtdAjq',
	'任黎明',
	'17338158607',
	'renlm@21cn.com',
	'admin',
	1
);

-- 图形设计
DELETE FROM graph;
INSERT INTO graph (
	uuid, 
	name, 
	is_public
) VALUES (
	'198124BBCF284A40BB24CA315A7B8E36', 
	'演示DEMO',
	1
);