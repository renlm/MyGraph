-- 用户
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id                			BIGSERIAL 		PRIMARY KEY,
    user_id       				VARCHAR(32)		UNIQUE NOT NULL,
    username					VARCHAR(255)	UNIQUE NOT NULL,
    password       				TEXT			NOT NULL,
    nickname					VARCHAR(255)	NOT NULL,
    mobile						VARCHAR(30),
    email						VARCHAR(128),
    role						VARCHAR(64),
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     				TIMESTAMP,
    disabled                	BOOLEAN 		NOT NULL DEFAULT FALSE
)WITH(OIDS=FALSE);
COMMENT ON TABLE users 										IS '用户';
COMMENT ON COLUMN users.id                					IS '主键ID';
COMMENT ON COLUMN users.user_id       						IS '用户ID';
COMMENT ON COLUMN users.username       						IS '登录账号';
COMMENT ON COLUMN users.password       						IS '密码';
COMMENT ON COLUMN users.nickname       						IS '昵称';
COMMENT ON COLUMN users.mobile       						IS '手机号码';
COMMENT ON COLUMN users.email                    			IS '邮箱地址';
COMMENT ON COLUMN users.role                    			IS '角色（admin，user，self）';
COMMENT ON COLUMN users.created_at                    		IS '创建时间';
COMMENT ON COLUMN users.updated_at                    		IS '更新时间';
COMMENT ON COLUMN users.disabled                       		IS '是否禁用（默认否）';

-- 数据源
DROP TABLE IF EXISTS ds;
CREATE TABLE ds (
    id                			BIGSERIAL 		PRIMARY KEY,
    uuid       					VARCHAR(32)		UNIQUE NOT NULL,
    url							VARCHAR(500)	NOT NULL,
    schema       				VARCHAR(255),
    username					VARCHAR(255)	NOT NULL,
    password					VARCHAR(255)	NOT NULL,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
COMMENT ON TABLE ds 										IS '数据源';
COMMENT ON COLUMN ds.id                						IS '主键ID';
COMMENT ON COLUMN ds.uuid       							IS 'UUID';
COMMENT ON COLUMN ds.url       								IS 'JDBC链接';
COMMENT ON COLUMN ds.schema       							IS '模式';
COMMENT ON COLUMN ds.username       						IS '账号';
COMMENT ON COLUMN ds.password       						IS '密码';
COMMENT ON COLUMN ds.created_at                    			IS '创建时间';
COMMENT ON COLUMN ds.creator_user_id                    	IS '创建人（用户ID）';
COMMENT ON COLUMN ds.creator_nickname                   	IS '创建人（昵称）';
COMMENT ON COLUMN ds.updated_at                    			IS '更新时间';
COMMENT ON COLUMN ds.updator_user_id                    	IS '更新人（用户ID）';
COMMENT ON COLUMN ds.updator_nickname                  		IS '更新人（昵称）';
COMMENT ON COLUMN ds.deleted                       			IS '是否删除（默认否）';
COMMENT ON COLUMN ds.remark                        			IS '备注';

-- ER模型
DROP TABLE IF EXISTS er;
CREATE TABLE er (
    id                			BIGSERIAL 		PRIMARY KEY,
    uuid       					VARCHAR(32)		UNIQUE NOT NULL,
    table_name       			VARCHAR(255)	NOT NULL,
    comment       				VARCHAR(500),
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
COMMENT ON TABLE er 										IS 'ER模型';
COMMENT ON COLUMN er.id                						IS '主键ID';
COMMENT ON COLUMN er.uuid       							IS 'UUID';
COMMENT ON COLUMN er.table_name       						IS '表名';
COMMENT ON COLUMN er.comment       							IS '注释';
COMMENT ON COLUMN er.created_at                    			IS '创建时间';
COMMENT ON COLUMN er.creator_user_id                  		IS '创建人（用户ID）';
COMMENT ON COLUMN er.creator_nickname             			IS '创建人（昵称）';
COMMENT ON COLUMN er.updated_at                    			IS '更新时间';
COMMENT ON COLUMN er.updator_user_id                		IS '更新人（用户ID）';
COMMENT ON COLUMN er.updator_nickname               		IS '更新人（昵称）';
COMMENT ON COLUMN er.deleted                       			IS '是否删除（默认否）';
COMMENT ON COLUMN er.remark                        			IS '备注';

-- 数据源-ER模型关系
DROP TABLE IF EXISTS ds_er_rel;
CREATE TABLE ds_er_rel (
    id                			BIGSERIAL 		PRIMARY KEY,
    ds_id						BIGINT			NOT NULL,
    er_id						BIGINT			NOT NULL,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE
)WITH(OIDS=FALSE);
COMMENT ON TABLE ds_er_rel 									IS '数据源-ER模型关系';
COMMENT ON COLUMN ds_er_rel.id                				IS '主键ID';
COMMENT ON COLUMN ds_er_rel.ds_id                			IS '数据源表主键ID';
COMMENT ON COLUMN ds_er_rel.er_id                			IS 'ER模型表主键ID';
COMMENT ON COLUMN ds_er_rel.created_at                      IS '创建时间';
COMMENT ON COLUMN ds_er_rel.creator_user_id              	IS '创建人（用户ID）';
COMMENT ON COLUMN ds_er_rel.creator_nickname             	IS '创建人（昵称）';
COMMENT ON COLUMN ds_er_rel.updated_at                  	IS '更新时间';
COMMENT ON COLUMN ds_er_rel.updator_user_id               	IS '更新人（用户ID）';
COMMENT ON COLUMN ds_er_rel.updator_nickname             	IS '更新人（昵称）';
COMMENT ON COLUMN ds_er_rel.deleted                      	IS '是否删除（默认否）';

-- ER模型-字段
DROP TABLE IF EXISTS er_field;
CREATE TABLE er_field (
    id                			BIGSERIAL 		PRIMARY KEY,
    er_id                		BIGINT			NOT NULL,
    uuid       					VARCHAR(32)		UNIQUE NOT NULL,
    name       					VARCHAR(255)	NOT NULL,
    comment       				VARCHAR(500),
    type       					INT				NOT NULL,
    type_name      				VARCHAR(255)	NOT NULL,
    size       					INT,
    digit       				INT,
    is_nullable                	BOOLEAN,
    auto_increment              BOOLEAN,
    column_def              	VARCHAR(255),
    is_pk                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    is_fk                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
COMMENT ON TABLE er_field 									IS 'ER模型-字段';
COMMENT ON COLUMN er_field.id                				IS '主键ID';
COMMENT ON COLUMN er_field.er_id                			IS 'ER模型ID';
COMMENT ON COLUMN er_field.uuid       						IS 'UUID';
COMMENT ON COLUMN er_field.name       						IS '列名';
COMMENT ON COLUMN er_field.comment       					IS '注释';
COMMENT ON COLUMN er_field.type       						IS '类型，java.sql.Types';
COMMENT ON COLUMN er_field.type_name       					IS '类型名称';
COMMENT ON COLUMN er_field.size       						IS '精度';
COMMENT ON COLUMN er_field.digit       						IS '标度';
COMMENT ON COLUMN er_field.is_nullable       				IS '是否可为空';
COMMENT ON COLUMN er_field.auto_increment       			IS '是否自增';
COMMENT ON COLUMN er_field.column_def       				IS '字段默认值';
COMMENT ON COLUMN er_field.is_pk       						IS '是否为主键（默认否）';
COMMENT ON COLUMN er_field.is_fk       						IS '是否为外键（默认否）';
COMMENT ON COLUMN er_field.created_at                    	IS '创建时间';
COMMENT ON COLUMN er_field.creator_user_id               	IS '创建人（用户ID）';
COMMENT ON COLUMN er_field.creator_nickname             	IS '创建人（昵称）';
COMMENT ON COLUMN er_field.updated_at                    	IS '更新时间';
COMMENT ON COLUMN er_field.updator_user_id                	IS '更新人（用户ID）';
COMMENT ON COLUMN er_field.updator_nickname               	IS '更新人（昵称）';
COMMENT ON COLUMN er_field.deleted                       	IS '是否删除（默认否）';
COMMENT ON COLUMN er_field.remark                        	IS '备注';

-- 图形设计
DROP TABLE IF EXISTS graph;
CREATE TABLE graph (
    id                			BIGSERIAL 		PRIMARY KEY,
    uuid       					VARCHAR(32)		UNIQUE NOT NULL,
    name       					VARCHAR(255)	NOT NULL,
    category_code				VARCHAR(50),
    category_name				VARCHAR(255),
    is_public					BOOLEAN 		NOT NULL DEFAULT FALSE,
    cover						TEXT,
    zoom						DECIMAL(10, 4) 	NOT NULL DEFAULT 1,
    dx							INT 			NOT NULL DEFAULT 0,
    dy							INT 			NOT NULL DEFAULT 0,
    grid_enabled				BOOLEAN 		NOT NULL DEFAULT TRUE,
    grid_size					INT 			NOT NULL DEFAULT 1,
    grid_color					VARCHAR(255),
    page_visible				BOOLEAN 		NOT NULL DEFAULT FALSE,
    background       			VARCHAR(255),
    connection_arrows_enabled	BOOLEAN 		NOT NULL DEFAULT FALSE,
    connectable					BOOLEAN 		NOT NULL DEFAULT TRUE,
    guides_enabled				BOOLEAN 		NOT NULL DEFAULT TRUE,
    xml       					TEXT,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     				TIMESTAMP,
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(255)
)WITH(OIDS=FALSE);
COMMENT ON TABLE graph 										IS '图形设计';
COMMENT ON COLUMN graph.id                					IS '主键ID';
COMMENT ON COLUMN graph.uuid       							IS 'UUID';
COMMENT ON COLUMN graph.name       							IS '名称';
COMMENT ON COLUMN graph.category_code       				IS '图形分类（编码）';
COMMENT ON COLUMN graph.category_name       				IS '图形分类（名称）';
COMMENT ON COLUMN graph.is_public       					IS '是否公开（默认否）';
COMMENT ON COLUMN graph.cover       						IS '封面图片';
COMMENT ON COLUMN graph.zoom								IS '缩放比例（默认1）';
COMMENT ON COLUMN graph.dx									IS '水平偏移量（默认0）';
COMMENT ON COLUMN graph.dy									IS '垂直偏移量（默认0）';
COMMENT ON COLUMN graph.grid_enabled						IS '显示网格（默认是）';
COMMENT ON COLUMN graph.grid_size							IS '网格大小（默认1）';
COMMENT ON COLUMN graph.grid_color							IS '网格颜色';
COMMENT ON COLUMN graph.page_visible						IS '页面视图（默认否）';	
COMMENT ON COLUMN graph.background       					IS '背景色';
COMMENT ON COLUMN graph.connection_arrows_enabled			IS '显示连接箭头（默认否）';
COMMENT ON COLUMN graph.connectable							IS '显示连接点（默认是）';
COMMENT ON COLUMN graph.guides_enabled						IS '显示参考线（默认是）';
COMMENT ON COLUMN graph.xml       							IS 'XML文本';
COMMENT ON COLUMN graph.created_at                  		IS '创建时间';
COMMENT ON COLUMN graph.updated_at               			IS '更新时间';
COMMENT ON COLUMN graph.deleted                    			IS '是否删除（默认否）';
COMMENT ON COLUMN graph.remark                      		IS '备注';