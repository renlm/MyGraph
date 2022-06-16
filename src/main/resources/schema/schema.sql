-- 用户
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user(
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    user_id            			CHAR(32)		UNIQUE			NOT NULL		COMMENT '用户ID',
    username                   	VARCHAR(255)    UNIQUE 			NOT NULL 		COMMENT '登录账号',
    password                  	VARCHAR(255)    				NOT NULL 		COMMENT '密码',
    nickname                 	VARCHAR(255)    				NOT NULL 		COMMENT '昵称',
    realname                 	VARCHAR(255)    				 				COMMENT '真实姓名',
    birthday                   	DATE    										COMMENT '出生日期',
    sex                   		VARCHAR(1)    									COMMENT '性别，M：男，F：女',
    mobile                   	VARCHAR(30)    									COMMENT '手机号码',
    email                    	VARCHAR(128)    								COMMENT '邮箱地址',
    sign                    	VARCHAR(255)    								COMMENT '个性签名',
    avatar                    	VARCHAR(32)    									COMMENT '头像',
    enabled                  	TINYINT(1)      DEFAULT 1  		NOT NULL  		COMMENT '是否启用（默认启用）',
    account_non_expired         TINYINT(1)      DEFAULT 1  		NOT NULL  		COMMENT '账户未过期（默认未过期）',
    credentials_non_expired		TINYINT(1)		DEFAULT 1  		NOT NULL  		COMMENT '凭证未过期（默认未过期）',
    account_non_locked        	TINYINT(1)     	DEFAULT 1 		NOT NULL 		COMMENT '账号未锁定（默认未锁定）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(800) COMMENT '备注',
    INDEX mobile(mobile),
    INDEX email(email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '用户';

-- 角色
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role(
	id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    role_id     				CHAR(32)		UNIQUE			NOT NULL		COMMENT '角色ID',
    code     					VARCHAR(20)   	UNIQUE			NOT NULL 		COMMENT '代码',
    text     					VARCHAR(255)   					NOT NULL 		COMMENT '名称',
    icon_cls           			VARCHAR(255)     								COMMENT '图标',
    level      					INT             				NOT NULL 		COMMENT '层级',
    sort            			INT             								COMMENT '排序',
    state						VARCHAR(20)						NOT NULL		COMMENT '展开状态，open：无子角色、closed：有子角色',
    pid           				BIGINT      									COMMENT '父级id',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    disabled TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否禁用（默认否）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(800) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '角色';

-- 组织机构
DROP TABLE IF EXISTS sys_org;
CREATE TABLE sys_org(
	id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    org_id     					CHAR(32)		UNIQUE			NOT NULL		COMMENT '组织机构ID',
    code     					VARCHAR(20)   	UNIQUE			NOT NULL 		COMMENT '代码',
    text     					VARCHAR(255)   					NOT NULL 		COMMENT '名称',
    org_type_code				VARCHAR(20)             		NOT NULL 		COMMENT '机构类型（编码）',
    leader_user_id				VARCHAR(32)      					 			COMMENT '负责人（用户ID）',
    icon_cls           			VARCHAR(255)     								COMMENT '图标',
    level      					INT             				NOT NULL 		COMMENT '层级',
    sort            			INT             								COMMENT '排序',
    state						VARCHAR(20)						NOT NULL		COMMENT '展开状态，open：无子节点、closed：有子节点',
    pid           				BIGINT      									comment '父级ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    disabled TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否禁用（默认否）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(800) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '组织机构';

-- 用户角色关系
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role(
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    sys_user_id       			BIGINT      					NOT NULL 		COMMENT '用户表（主键ID）',
    sys_role_id       			BIGINT      					NOT NULL 		COMMENT '角色表（主键ID）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '用户角色关系';

-- 用户组织机构关系
DROP TABLE IF EXISTS sys_user_org;
CREATE TABLE sys_user_org(
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    sys_user_id       			BIGINT      					NOT NULL 		COMMENT '用户表（主键ID）',
    sys_org_id       			BIGINT      					NOT NULL 		COMMENT '组织机构表（主键ID）',
    position_code				VARCHAR(255)									COMMENT '职位编码',
    position_name				VARCHAR(255)									COMMENT '职位名称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '用户组织机构关系';

-- 资源
DROP TABLE IF EXISTS sys_resource;
CREATE TABLE sys_resource(
	id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    resource_id     			CHAR(32)		UNIQUE			NOT NULL		COMMENT '资源ID',
    code     					VARCHAR(20)   	UNIQUE			NOT NULL 		COMMENT '代码',
    text     					VARCHAR(255)   					NOT NULL 		COMMENT '名称',
    resource_type_code			VARCHAR(20)             		NOT NULL 		COMMENT '资源类型（编码）',
    url      					VARCHAR(500)     								COMMENT '资源地址或标识',
    icon_cls           			VARCHAR(255)     								COMMENT '图标',
    icon_cls_colour           	VARCHAR(20)     								COMMENT '图标颜色',
    text_colour           		VARCHAR(20)     								COMMENT '资源名称颜色',
    level      					INT             				NOT NULL 		COMMENT '层级',
    sort            			INT             								COMMENT '排序',
    state						VARCHAR(20)						NOT NULL		COMMENT '展开状态，open：无子菜单、closed：有子菜单',
    default_home_page			TINYINT(1) 		DEFAULT 0						COMMENT '是否为默认主页（默认否）',
    pid           				BIGINT      									COMMENT '父级ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    disabled TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否禁用（默认否）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(800) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '资源';

-- 角色资源关系
DROP TABLE IF EXISTS sys_role_resource;
CREATE TABLE sys_role_resource(
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    sys_role_id       			BIGINT      					NOT NULL 		COMMENT '角色表（主键ID）',
    sys_resource_id       		BIGINT      					NOT NULL 		COMMENT '资源表（主键ID）',
    alias     					VARCHAR(255)   					                COMMENT '别名',
    sort            			INT             								COMMENT '排序',
    default_home_page			TINYINT(1) 										COMMENT '是否为默认主页',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '角色资源关系';

-- 系统常量
DROP TABLE IF EXISTS sys_const;
CREATE TABLE sys_const(
    const_id            		CHAR(32)      	PRIMARY KEY 					COMMENT '常量ID',
    code     					VARCHAR(128)   	UNIQUE			NOT NULL 		COMMENT '代码',
    name     					VARCHAR(255)   					NOT NULL 		COMMENT '名称',
    val     					VARCHAR(255)   					NOT NULL 		COMMENT '值',
    sort            			INT             								COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(800) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '系统常量';

-- 数据字典
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict(
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    uuid     					VARCHAR(32)   	UNIQUE			NOT NULL 		COMMENT 'UUID',
    code     					VARCHAR(20)   					NOT NULL 		COMMENT '代码',
    text     					VARCHAR(255)   					NOT NULL 		COMMENT '名称',
    abbreviation     			VARCHAR(255)   					                COMMENT '简称',
    alias     					VARCHAR(255)   					                COMMENT '别名',
    icon_cls           			VARCHAR(255)     								COMMENT '图标',
    level      					INT             				NOT NULL 		COMMENT '层级',
    sort            			INT             								COMMENT '排序',
    state						VARCHAR(20)						NOT NULL		COMMENT '展开状态，open：无子节点、closed：有子节点',
    pid           				BIGINT      									COMMENT '父级ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    disabled TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否禁用（默认否）',
    remark VARCHAR(800) COMMENT '备注',
    INDEX code(code),
    INDEX level(level),
    INDEX sort(sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '数据字典';

-- 文件
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file(
	id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    file_id            			VARCHAR(32)   	UNIQUE			NOT NULL 		COMMENT '文件ID',
    original_filename       	VARCHAR(255) 									COMMENT '文件名',
    is_public        			TINYINT(1) 		DEFAULT 0 		NOT NULL 		COMMENT '是否公开（默认否）',
    file_type       			VARCHAR(255) 									COMMENT '文件类型',
    file_content       			LONGBLOB 										COMMENT '文件内容',
    size       					BIGINT 											COMMENT '文件大小',
    actuator       				VARCHAR(255) 									COMMENT '执行器',
    param_json       			TEXT 											COMMENT '执行参数（Json格式）',
    status       				INT 			DEFAULT 1						COMMENT '状态，1：正常，2：任务初始化，3：任务执行中，4：任务异常，5：任务已完成',
    message       				TEXT											COMMENT '消息内容（异常信息）',
    creator_user_id       		VARCHAR(32)										COMMENT '创建人（用户ID）',
    creator_nickname       		VARCHAR(255)									COMMENT '创建人（昵称）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(800) COMMENT '备注',
    INDEX creator_user_id(creator_user_id),
    INDEX created_at(created_at),
    INDEX deleted(deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '文件';

-- 数据源
DROP TABLE IF EXISTS ds;
CREATE TABLE ds (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    uuid       					VARCHAR(32)		UNIQUE 			NOT NULL		COMMENT 'UUID',
    name						VARCHAR(255)					NOT NULL		COMMENT '名称',
    url							VARCHAR(500)					NOT NULL		COMMENT 'JDBC链接',
    schema_name       			VARCHAR(255)									COMMENT '模式',
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
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '数据源-ER模型关系';

-- ER模型-字段
DROP TABLE IF EXISTS er_field;
CREATE TABLE er_field (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    er_id						BIGINT							NOT NULL		COMMENT 'ER模型ID',
    uuid       					VARCHAR(32)		UNIQUE 			NOT NULL		COMMENT 'UUID',
    name       					VARCHAR(255)					NOT NULL		COMMENT '列名',
    comment       				VARCHAR(500)									COMMENT '注释',
    sql_type       				INT								NOT NULL		COMMENT 'java.sql.Types',
    jdbc_type      				VARCHAR(255)					NOT NULL		COMMENT 'JdbcType',
    size       					BIGINT											COMMENT '长度',
    digit       				INT												COMMENT '精度',
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

-- ER模型-我的字段库
DROP TABLE IF EXISTS er_field_lib;
CREATE TABLE er_field_lib (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    uuid       					VARCHAR(32)		UNIQUE 			NOT NULL		COMMENT 'UUID',
    name       					VARCHAR(255)					NOT NULL		COMMENT '列名',
    comment       				VARCHAR(500)									COMMENT '注释',
    sql_type       				INT								NOT NULL		COMMENT 'java.sql.Types',
    jdbc_type      				VARCHAR(255)					NOT NULL		COMMENT 'JdbcType',
    size       					BIGINT											COMMENT '长度',
    digit       				INT												COMMENT '精度',
    is_nullable                	TINYINT(1)										COMMENT '是否可为空',
    auto_increment              TINYINT(1)										COMMENT '是否自增',
    column_def              	VARCHAR(255)									COMMENT '字段默认值',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = 'ER模型-我的字段库';

-- 文档项目
DROP TABLE IF EXISTS doc_project;
CREATE TABLE doc_project (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    uuid       					VARCHAR(32)		UNIQUE 			NOT NULL		COMMENT 'UUID',
    project_name       			VARCHAR(255)					NOT NULL		COMMENT '项目名称',
    visit_level       			INT				DEFAULT 1		NOT NULL		COMMENT '访问级别，1：私有（默认，只有项目成员才可以访问），2：公开（可以由任何登录用户访问）',
    is_share       				TINYINT(1)		DEFAULT 1		NOT NULL		COMMENT '是否允许分享（默认是，该项目下的文档是否允许分享）',
    tags       					VARCHAR(255)					                COMMENT '标签（多个逗号分隔）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注',
    INDEX creator_user_id(creator_user_id),
    INDEX deleted(deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '文档项目';

-- 文档项目-标签
DROP TABLE IF EXISTS doc_project_tag;
CREATE TABLE doc_project_tag (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    doc_project_id				BIGINT							NOT NULL		COMMENT '文档项目ID',
    tag_name       				VARCHAR(255)					NOT NULL		COMMENT '标签名称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    INDEX doc_project_id(doc_project_id),
    INDEX deleted(deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '文档项目-标签';

-- 文档项目-成员
DROP TABLE IF EXISTS doc_project_member;
CREATE TABLE doc_project_member (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    doc_project_id				BIGINT							NOT NULL		COMMENT '文档项目ID',
    member_user_id 				VARCHAR(32) 					NOT NULL		COMMENT '成员（用户ID）',
    role						INT				DEFAULT 1		NOT NULL		COMMENT '角色，1：浏览者，2：编辑者，3：管理员',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    INDEX doc_project_id(doc_project_id),
    INDEX member_user_id(member_user_id),
    INDEX deleted(deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '文档项目-成员';

-- 文档分类
DROP TABLE IF EXISTS doc_category;
CREATE TABLE doc_category (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    doc_project_id				BIGINT							NOT NULL		COMMENT '文档项目ID',
    uuid     					VARCHAR(32)   	UNIQUE			NOT NULL 		COMMENT 'UUID',
    text     					VARCHAR(255)   					NOT NULL 		COMMENT '名称',
    icon_cls           			VARCHAR(255)     								COMMENT '图标',
    level      					INT             				NOT NULL 		COMMENT '层级',
    sort            			INT             								COMMENT '排序',
    state						VARCHAR(20)						NOT NULL		COMMENT '展开状态，open：无子节点、closed：有子节点',
    pid           				BIGINT      									COMMENT '父级ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注',
    INDEX doc_project_id(doc_project_id),
    INDEX level(level),
    INDEX sort(sort),
    INDEX pid(pid),
    INDEX creator_user_id(creator_user_id),
    INDEX deleted(deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '文档分类';

-- 文档分类-收藏
DROP TABLE IF EXISTS doc_category_collect;
CREATE TABLE doc_category_collect (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    doc_category_id				BIGINT							NOT NULL		COMMENT '文档分类ID',
    member_user_id 				VARCHAR(32) 					NOT NULL		COMMENT '收藏人（用户ID）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    INDEX doc_category_id(doc_category_id),
    INDEX member_user_id(member_user_id),
    INDEX deleted(deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '文档分类-收藏';

-- 文档分类-分享
DROP TABLE IF EXISTS doc_category_share;
CREATE TABLE doc_category_share (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    doc_category_id				BIGINT							NOT NULL		COMMENT '文档分类ID',
    uuid     					VARCHAR(64)   	UNIQUE			NOT NULL 		COMMENT 'UUID',
    share_type					INT				DEFAULT 1		NOT NULL		COMMENT '分享类型，1：公开，2：密码查看',
    password					VARCHAR(255) 									COMMENT '访问密码',
    effective_type				INT				DEFAULT -1		NOT NULL		COMMENT '有效期类型（-1，永久，7:七天，30：三十天）',
    deadline					TIMESTAMP										COMMENT '有效截止时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    disabled TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否禁用（默认否）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注',
    INDEX doc_category_id(doc_category_id),
    INDEX deadline(deadline),
    INDEX creator_user_id(creator_user_id),
    INDEX deleted(deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '文档分类-分享';

-- Markdown文档
DROP TABLE IF EXISTS markdown;
CREATE TABLE markdown (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    uuid       					VARCHAR(32)		UNIQUE 			NOT NULL		COMMENT 'UUID',
    name       					VARCHAR(255)					NOT NULL		COMMENT '文档名称',
    version 					INT								NOT NULL		COMMENT '版本',
    content						LONGTEXT										COMMENT '文档内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = 'Markdown文档';

-- Markdown文档-历史记录
DROP TABLE IF EXISTS markdown_history;
CREATE TABLE markdown_history (
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    markdown_uuid       		VARCHAR(32)						NOT NULL		COMMENT 'Markdown文档UUID',
    name       					VARCHAR(255)					NOT NULL		COMMENT '文档名称',
    version 					INT								NOT NULL		COMMENT '版本',
    content						LONGTEXT										COMMENT '文档内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    creator_user_id 			VARCHAR(32) 								COMMENT '创建人（用户ID）',
    creator_nickname 			VARCHAR(255) 								COMMENT '创建人（昵称）',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updator_user_id 			VARCHAR(32) 								COMMENT '更新人（用户ID）',
    updator_nickname 			VARCHAR(255) 								COMMENT '更新人（昵称）',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = 'Markdown文档-历史记录';

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

-- 定时任务日志
DROP TABLE IF EXISTS `QRTZ_LOGS`;
CREATE TABLE `QRTZ_LOGS`(
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    machine						VARCHAR(128)									COMMENT '机器ip',	
    trigger_name				VARCHAR(128)									COMMENT '触发器',
    job_name					VARCHAR(128)									COMMENT '任务描述',
    job_class_name				VARCHAR(128)									COMMENT '执行类',
    job_data_map_json			TEXT											COMMENT '任务参数',
    batch            			VARCHAR(32)						NOT NULL        COMMENT '批次',
    seq            				INT             								COMMENT '序列',
    level						VARCHAR(10)										COMMENT '日志级别',
    text						TEXT											COMMENT '日志内容',
    created_at timestamp default current_timestamp comment '创建时间',
    INDEX trigger_name(trigger_name),
    INDEX job_name(job_name),
    INDEX batch(batch),
    INDEX seq(seq),
    INDEX created_at(created_at)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT = '定时任务日志';

-- 系统登录日志
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log(
    id            				BIGINT      	PRIMARY KEY 	AUTO_INCREMENT	COMMENT '主键ID',
    user_id            			VARCHAR(32)      				NOT NULL		COMMENT '用户ID',
    username                   	VARCHAR(255)    				NOT NULL		COMMENT '账号',
    nickname                 	VARCHAR(255)    								COMMENT '昵称',
    client_ip                 	VARCHAR(255)    								COMMENT '客户端ip',
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '登录时间',
    INDEX user_id(user_id),
    INDEX login_time(login_time)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT = '系统登录日志';