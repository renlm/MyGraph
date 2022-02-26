-- 图形设计
DROP TABLE IF EXISTS graph;
CREATE TABLE graph(
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