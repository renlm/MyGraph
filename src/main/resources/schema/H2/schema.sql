-- 图形设计
DROP TABLE IF EXISTS graph;
CREATE TABLE graph(
    id                			BIGSERIAL 		PRIMARY KEY,
    uuid       					VARCHAR(32)		UNIQUE NOT NULL,
    text       					VARCHAR(255)	NOT NULL,
    category					VARCHAR(255),
    is_public					BOOLEAN 		NOT NULL DEFAULT FALSE,
    cover						TEXT,
    zoom						DECIMAL(10, 4) 	NOT NULL DEFAULT 1,
    dx							INT 			NOT NULL DEFAULT 0,
    dy							INT 			NOT NULL DEFAULT 0,
    grid_enabled				BOOLEAN 		NOT NULL DEFAULT TRUE,
    grid_size					INT 			NOT NULL DEFAULT 1,
    grid_color					VARCHAR(255)	NOT NULL DEFAULT '#D0D0D0',
    page_visible				BOOLEAN 		NOT NULL DEFAULT FALSE,
    background       			VARCHAR(255),
    connection_arrows_enabled	BOOLEAN 		NOT NULL DEFAULT FALSE,
    connectable					BOOLEAN 		NOT NULL DEFAULT TRUE,
    guides_enabled				BOOLEAN 		NOT NULL DEFAULT TRUE,
    xml       					TEXT,
    from_uuid					VARCHAR(32),
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
COMMENT ON TABLE mxgraph 									IS '图形设计';
COMMENT ON COLUMN mxgraph.id                				IS '主键ID';
COMMENT ON COLUMN mxgraph.uuid       						IS 'UUID';
COMMENT ON COLUMN mxgraph.text       						IS '名称';
COMMENT ON COLUMN mxgraph.category       					IS '图形分类（编码）';
COMMENT ON COLUMN mxgraph.is_public       					IS '是否公开（默认否）';
COMMENT ON COLUMN mxgraph.cover       						IS '封面图片';
COMMENT ON COLUMN mxgraph.zoom								IS '缩放比例（默认1）';
COMMENT ON COLUMN mxgraph.dx								IS '水平偏移量（默认0）';
COMMENT ON COLUMN mxgraph.dy								IS '垂直偏移量（默认0）';
COMMENT ON COLUMN mxgraph.grid_enabled						IS '显示网格（默认是）';
COMMENT ON COLUMN mxgraph.grid_size							IS '网格大小（默认1）';
COMMENT ON COLUMN mxgraph.grid_color						IS '网格颜色（默认#D0D0D0）';
COMMENT ON COLUMN mxgraph.page_visible						IS '页面视图（默认否）';	
COMMENT ON COLUMN mxgraph.background       					IS '背景色';
COMMENT ON COLUMN mxgraph.connection_arrows_enabled			IS '显示链接箭头（默认否）';
COMMENT ON COLUMN mxgraph.connectable						IS '显示连接点（默认是）';
COMMENT ON COLUMN mxgraph.guides_enabled					IS '显示参考线（默认是）';
COMMENT ON COLUMN mxgraph.xml       						IS 'XML文本';
COMMENT ON COLUMN mxgraph.from_uuid                    		IS '创建来源（UUID）';
COMMENT ON COLUMN mxgraph.created_at                  		IS '创建时间';
COMMENT ON COLUMN mxgraph.creator_user_id            		IS '创建人（用户ID）';
COMMENT ON COLUMN mxgraph.creator_nickname            		IS '创建人（昵称）';
COMMENT ON COLUMN mxgraph.updated_at               			IS '更新时间';
COMMENT ON COLUMN mxgraph.updator_user_id            		IS '更新人（用户ID）';
COMMENT ON COLUMN mxgraph.updator_nickname           		IS '更新人（昵称）';
COMMENT ON COLUMN mxgraph.deleted                    		IS '是否删除（默认否）';
COMMENT ON COLUMN mxgraph.remark                      		IS '备注';