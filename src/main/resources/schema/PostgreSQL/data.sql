-- 爬虫
DELETE FROM spider;
INSERT INTO spider
	   (spider_id, 					spider_name,	thread_num,	creator_user_id, 					creator_nickname,	remark)
VALUES ('1481984066382835712', 		'公示列表',		2,			'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲',				'公共资源交易')
;

-- 图形设计-图库组
DELETE FROM mxgraph_group;
INSERT INTO mxgraph_group
	   (uuid, 								text, 					icon_cls,					level, sort, state, 	pid,	creator_user_id, 					creator_nickname)
VALUES ('BD1B4E56AB4543A99CCD2C36C7AB900C',	'Crawler 管理后台',		'fa fa-twitter',			1,	   1,	 'closed',  null, 	'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲'),
	   ('7905AC314D4947E48076E7BCD45126AF',	'平台管理',				'fa fa-cog',				2,	   1,	 'open',    1, 		'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲'),
	   ('F15BBDB330BD44CA96140290CF2800B6',	'网络爬虫',				'fa fa-bug',				2,	   2,	 'open',    1, 		'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲'),
	   ('7DC0B8992EE74B3EBA891E14CBCFF4E4',	'数据源',					'fa fa-soccer-ball-o',		2,	   3,	 'open',    1, 		'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲'),
	   ('28E92BF5A710476E9FEFC317177A72D8',	'图形设计',				'fa fa-audio-description',	2,	   4,	 'open',    1, 		'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲')
;

-- 图形设计-图库组
DELETE FROM mxgraph;
INSERT INTO mxgraph
	   (uuid, 								text, 		category,	is_public,	creator_user_id, 					creator_nickname, updated_at)
VALUES ('198124BBCF284A40BB24CA315A7B8E36',	'演示DEMO',	'ER',		true,		'ABFEE20BA6AC4783A683C7117763951E',	'令狐冲',			  now())
;

-- 图形设计-图库组关系
DELETE FROM mxgraph_group_rel;
INSERT INTO mxgraph_group_rel(
	mxgraph_group_id, 
	mxgraph_id,	
	creator_user_id, 					
	creator_nickname
) SELECT mg.id AS mxgraph_group_id,
	m.id AS mxgraph_id,
	'ABFEE20BA6AC4783A683C7117763951E' AS creator_user_id,	
	'令狐冲' AS creator_nickname
FROM mxgraph_group mg, mxgraph m 
WHERE mg.uuid = '28E92BF5A710476E9FEFC317177A72D8'
	AND m.uuid = '198124BBCF284A40BB24CA315A7B8E36'
;