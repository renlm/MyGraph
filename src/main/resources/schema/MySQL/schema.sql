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
    connection_arrows_enabled	TINYINT(1) 		NOT NULL DEFAULT 0			COMMENT '显示链接箭头（默认否）',
    connectable					TINYINT(1) 		NOT NULL DEFAULT 1			COMMENT '显示连接点（默认是）',
    guides_enabled				TINYINT(1) 		NOT NULL DEFAULT 1			COMMENT '显示参考线（默认是）',
    xml       					TEXT										COMMENT 'XML文本',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否删除（默认否）',
    remark VARCHAR(800) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '图形设计';