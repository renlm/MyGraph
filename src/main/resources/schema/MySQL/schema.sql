-- 用户
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    user_id            			CHAR(32)		UNIQUE			NOT NULL		COMMENT '用户ID',
    username                   	VARCHAR(255)    UNIQUE 			NOT NULL 		COMMENT '登录账号',
    password                  	TEXT    						NOT NULL 		COMMENT '密码',
    nickname                 	VARCHAR(255)    				NOT NULL 		COMMENT '昵称',
    mobile                   	VARCHAR(30)    									COMMENT '手机号码',
    email                    	VARCHAR(128)    								COMMENT '邮箱地址',
    role						VARCHAR(64)										COMMENT '角色（admin，user，self）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    disabled TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否禁用（默认否）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '用户';

-- 数据源
DROP TABLE IF EXISTS ds;
CREATE TABLE ds (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    uuid       					VARCHAR(32)		UNIQUE 			NOT NULL		COMMENT 'UUID',
    url							VARCHAR(500)					NOT NULL		COMMENT 'JDBC链接',
    schema       				VARCHAR(255)									COMMENT '模式',
    username					VARCHAR(255)					NOT NULL		COMMENT '账号',
    password					VARCHAR(255)					NOT NULL		COMMENT '密码',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '数据源';

-- ER模型
DROP TABLE IF EXISTS er;
CREATE TABLE er (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    uuid       					VARCHAR(32)		UNIQUE 			NOT NULL		COMMENT 'UUID',
    table_name       			VARCHAR(255)					NOT NULL		COMMENT '表名',
    comment       				VARCHAR(500)									COMMENT '注释',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = 'ER模型';

-- 数据源-ER模型关系
DROP TABLE IF EXISTS ds_er_rel;
CREATE TABLE ds_er_rel (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    ds_id						BIGINT							NOT NULL		COMMENT '数据源表主键ID',
    er_id						BIGINT							NOT NULL		COMMENT 'ER模型表主键ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '数据源-ER模型关系';

-- ER模型-字段
DROP TABLE IF EXISTS er_field;
CREATE TABLE er_field (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    er_id						BIGINT							NOT NULL		COMMENT 'ER模型表主键ID',
    uuid       					VARCHAR(32)		UNIQUE 			NOT NULL		COMMENT 'UUID',
    name       					VARCHAR(255)					NOT NULL		COMMENT '列名',
    comment       				VARCHAR(500)									COMMENT '注释',
    type       					INT								NOT NULL		COMMENT '类型，java.sql.Types',
    type_name      				VARCHAR(255)					NOT NULL		COMMENT '类型名称',
    size       					INT												COMMENT '精度',
    digit       				INT												COMMENT '标度',
    is_nullable                	TINYINT(1)										COMMENT '是否可为空',
    auto_increment              TINYINT(1)										COMMENT '是否自增',
    column_def              	VARCHAR(255)									COMMENT '字段默认值',
    is_pk                		TINYINT(1) 		DEFAULT 0		NOT NULL		COMMENT '是否为主键（默认否）',
    is_fk                		TINYINT(1) 		DEFAULT 0		NOT NULL		COMMENT '是否为外键（默认否）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = 'ER模型-字段';

-- 图形设计
DROP TABLE IF EXISTS graph;
CREATE TABLE graph(
    id							BIGINT(20) 		PRIMARY KEY AUTO_INCREMENT	COMMENT '主键ID',
    uuid       					VARCHAR(32)		UNIQUE NOT NULL				COMMENT 'UUID',
    name       					VARCHAR(255)	NOT NULL					COMMENT '名称',
    category_code				VARCHAR(50)									COMMENT '图形分类（编码）',
    category_name				VARCHAR(255)								COMMENT '图形分类（名称）',
    is_public					TINYINT(1) 		NOT NULL DEFAULT 0			COMMENT '是否公开（默认否）',
    cover						VARCHAR(32)									COMMENT '封面图片',
    zoom						DECIMAL(10, 4) 	NOT NULL DEFAULT 1			COMMENT '缩放比例（默认1）',
    dx							INT 			NOT NULL DEFAULT 0			COMMENT '水平偏移量（默认0）',
    dy							INT 			NOT NULL DEFAULT 0			COMMENT '垂直偏移量（默认0）',
    grid_enabled				TINYINT(1) 		NOT NULL DEFAULT 1			COMMENT '显示网格（默认是）',
    grid_size					INT 			NOT NULL DEFAULT 1			COMMENT '网格大小（默认1）',
    grid_color					VARCHAR(255)								COMMENT '网格颜色',
    page_visible				TINYINT(1) 		NOT NULL DEFAULT 0			COMMENT '页面视图（默认否）',
    background       			VARCHAR(255)								COMMENT '背景色',
    connection_arrows_enabled	TINYINT(1) 		NOT NULL DEFAULT 0			COMMENT '显示连接箭头（默认否）',
    connectable					TINYINT(1) 		NOT NULL DEFAULT 1			COMMENT '显示连接点（默认是）',
    guides_enabled				TINYINT(1) 		NOT NULL DEFAULT 1			COMMENT '显示参考线（默认是）',
    xml       					LONGTEXT									COMMENT 'XML文本',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '图形设计';