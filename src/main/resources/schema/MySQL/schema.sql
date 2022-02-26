-- 爬虫
DROP TABLE IF EXISTS spider;
CREATE TABLE spider(
    spider_id                	CHAR(19) 		PRIMARY KEY,
    spider_name       			VARCHAR(255)	NOT NULL,
    thread_num               	INT				DEFAULT 1,
    empty_sleep_time			INT				DEFAULT 30000,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
COMMENT ON TABLE spider 									IS '爬虫';
COMMENT ON COLUMN spider.spider_id                			IS '爬虫ID';
COMMENT ON COLUMN spider.spider_name       					IS '爬虫名称';
COMMENT ON COLUMN spider.thread_num               			IS '开启线程数';
COMMENT ON COLUMN spider.empty_sleep_time               	IS 'Url轮询空闲等待时间（毫秒，默认30000）';
COMMENT ON COLUMN spider.created_at                    		IS '创建时间';
COMMENT ON COLUMN spider.creator_user_id                    IS '创建人（用户ID）';
COMMENT ON COLUMN spider.creator_nickname                   IS '创建人（昵称）';
COMMENT ON COLUMN spider.updated_at                    		IS '更新时间';
COMMENT ON COLUMN spider.updator_user_id                    IS '更新人（用户ID）';
COMMENT ON COLUMN spider.updator_nickname                  	IS '更新人（昵称）';
COMMENT ON COLUMN spider.deleted                       		IS '是否删除（默认否）';
COMMENT ON COLUMN spider.remark                        		IS '备注';

-- 爬虫-站点
DROP TABLE IF EXISTS spider_site;
CREATE TABLE spider_site(
    site_id              		CHAR(19) 		PRIMARY KEY,
    spider_id					CHAR(19)		NOT NULL,
    site_name       			VARCHAR(255),
    domain                 		VARCHAR(255),
    user_agent               	VARCHAR(255),
    charset               		VARCHAR(20),
    enable_selenuim             BOOLEAN			DEFAULT FALSE,
    headless             		BOOLEAN			DEFAULT TRUE,
    screenshot             		BOOLEAN			DEFAULT FALSE,
    save_html             		BOOLEAN			DEFAULT FALSE,
    enable_qrtz             	BOOLEAN			DEFAULT FALSE,
    invalid_param_names         VARCHAR(255),
    sleep_time               	INT				DEFAULT 5000,
    retry_times               	INT				DEFAULT 0,
    cycle_retry_times           INT				DEFAULT 1,
    retry_sleep_time            INT				DEFAULT 1000,
    time_out               		INT				DEFAULT 5000,
    use_gzip               		BOOLEAN 		DEFAULT TRUE,
    disable_cookie_management   BOOLEAN 		DEFAULT FALSE,
    crawl_processor_version		VARCHAR(255),
    crawl_processor				TEXT,
    start_up_time     			TIMESTAMP,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
CREATE INDEX IdxSpiderSite_spider_id ON spider_site(spider_id);
COMMENT ON TABLE spider_site 								IS '爬虫-站点';
COMMENT ON COLUMN spider_site.site_id                		IS '站点ID';
COMMENT ON COLUMN spider_site.spider_id       				IS '爬虫ID';
COMMENT ON COLUMN spider_site.site_name       				IS '站点名称';
COMMENT ON COLUMN spider_site.domain                 		IS '域名（Cookie指定域时生效必须）';
COMMENT ON COLUMN spider_site.user_agent               		IS 'UserAgent';
COMMENT ON COLUMN spider_site.charset               		IS '编码';
COMMENT ON COLUMN spider_site.enable_selenuim               IS '是否启用浏览器模式（默认否）';
COMMENT ON COLUMN spider_site.headless               		IS '无头模式（浏览器模式下，默认是）';
COMMENT ON COLUMN spider_site.screenshot               		IS '保存页面截图（浏览器模式下，默认否）';
COMMENT ON COLUMN spider_site.save_html               		IS '是否保存网页内容（默认否）';
COMMENT ON COLUMN spider_site.enable_qrtz               	IS '是否启用定时任务（默认否）';
COMMENT ON COLUMN spider_site.invalid_param_names           IS '无效参数（多个逗号分隔，从Url删除此参数，降低重复请求次数）';
COMMENT ON COLUMN spider_site.sleep_time               		IS '休眠时间（毫秒，默认5000）';
COMMENT ON COLUMN spider_site.retry_times               	IS '重试次数（默认0，暂未启用）';
COMMENT ON COLUMN spider_site.cycle_retry_times           	IS '循环重试次数（默认1，用于网页下载失败时重试，重试时忽略Url缓存）';
COMMENT ON COLUMN spider_site.retry_sleep_time            	IS '重试休眠时间（毫秒，默认1000）';
COMMENT ON COLUMN spider_site.time_out               		IS '超时时间（毫秒，默认5000）';
COMMENT ON COLUMN spider_site.use_gzip               		IS '是否启用Gzip（默认是）';
COMMENT ON COLUMN spider_site.disable_cookie_management   	IS '是否禁用Cookie（默认否）';
COMMENT ON COLUMN spider_site.crawl_processor_version   	IS '抓取器版本号（类名）';
COMMENT ON COLUMN spider_site.crawl_processor   			IS '抓取器（Java代码，提取字段）';
COMMENT ON COLUMN spider_site.start_up_time                 IS '启动时间（记录最后一次）';
COMMENT ON COLUMN spider_site.created_at                    IS '创建时间';
COMMENT ON COLUMN spider_site.creator_user_id               IS '创建人（用户ID）';
COMMENT ON COLUMN spider_site.creator_nickname              IS '创建人（昵称）';
COMMENT ON COLUMN spider_site.updated_at                    IS '更新时间';
COMMENT ON COLUMN spider_site.updator_user_id               IS '更新人（用户ID）';
COMMENT ON COLUMN spider_site.updator_nickname              IS '更新人（昵称）';
COMMENT ON COLUMN spider_site.deleted                       IS '是否删除（默认否）';
COMMENT ON COLUMN spider_site.remark                        IS '备注';

-- 爬虫-站点-入口链接
DROP TABLE IF EXISTS spider_site_starturl;
CREATE TABLE spider_site_starturl(
    starturl_id                 CHAR(32) 		PRIMARY KEY,
    spider_id					CHAR(19)		NOT NULL,
    site_id						CHAR(19)		NOT NULL,
    flag						VARCHAR(255),
    url							VARCHAR(500),
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32),
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
CREATE INDEX IdxSpiderSiteStarturl_site_id ON spider_site_starturl(site_id);
COMMENT ON TABLE spider_site_starturl 						IS '爬虫-站点-入口链接';
COMMENT ON COLUMN spider_site_starturl.starturl_id          IS '入口链接ID';
COMMENT ON COLUMN spider_site_starturl.spider_id       		IS '爬虫ID';
COMMENT ON COLUMN spider_site_starturl.site_id       		IS '站点ID';
COMMENT ON COLUMN spider_site_starturl.flag       			IS '标记值';
COMMENT ON COLUMN spider_site_starturl.url       			IS '页面链接';
COMMENT ON COLUMN spider_site_starturl.created_at           IS '创建时间';
COMMENT ON COLUMN spider_site_starturl.creator_user_id      IS '创建人（用户ID）';
COMMENT ON COLUMN spider_site_starturl.creator_nickname     IS '创建人（昵称）';
COMMENT ON COLUMN spider_site_starturl.updated_at           IS '更新时间';
COMMENT ON COLUMN spider_site_starturl.updator_user_id      IS '更新人（用户ID）';
COMMENT ON COLUMN spider_site_starturl.updator_nickname     IS '更新人（昵称）';
COMMENT ON COLUMN spider_site_starturl.deleted              IS '是否删除（默认否）';
COMMENT ON COLUMN spider_site_starturl.remark               IS '备注';

-- 爬虫-站点-Url匹配正则
DROP TABLE IF EXISTS spider_site_urlregex;
CREATE TABLE spider_site_urlregex(
    urlregex_id                 CHAR(32) 		PRIMARY KEY,
    spider_id					CHAR(19)		NOT NULL,
    site_id						CHAR(19)		NOT NULL,
    regex						VARCHAR(255),
    regex_group					INT,
    page_url_type				INT,
    clean_params             	BOOLEAN			DEFAULT FALSE,
    depth						INT				DEFAULT 0,
    flag						VARCHAR(255),
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32),
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
CREATE INDEX IdxSpiderSiteUrlregex_site_id ON spider_site_urlregex(site_id);
COMMENT ON TABLE spider_site_urlregex 						IS '爬虫-站点-Url匹配正则';
COMMENT ON COLUMN spider_site_urlregex.urlregex_id          IS 'Url匹配正则ID';
COMMENT ON COLUMN spider_site_urlregex.spider_id       		IS '爬虫ID';
COMMENT ON COLUMN spider_site_urlregex.site_id       		IS '站点ID';
COMMENT ON COLUMN spider_site_urlregex.regex       			IS '匹配正则';
COMMENT ON COLUMN spider_site_urlregex.regex_group       	IS '正则Group（默认0）';
COMMENT ON COLUMN spider_site_urlregex.page_url_type       	IS '页面链接类型，0：种子，1：数据';
COMMENT ON COLUMN spider_site_urlregex.clean_params         IS '是否清除Url参数（默认否）';
COMMENT ON COLUMN spider_site_urlregex.depth       			IS '爬取深度，默认0（无限制）';
COMMENT ON COLUMN spider_site_urlregex.flag       			IS '标记值';
COMMENT ON COLUMN spider_site_urlregex.created_at           IS '创建时间';
COMMENT ON COLUMN spider_site_urlregex.creator_user_id      IS '创建人（用户ID）';
COMMENT ON COLUMN spider_site_urlregex.creator_nickname     IS '创建人（昵称）';
COMMENT ON COLUMN spider_site_urlregex.updated_at           IS '更新时间';
COMMENT ON COLUMN spider_site_urlregex.updator_user_id      IS '更新人（用户ID）';
COMMENT ON COLUMN spider_site_urlregex.updator_nickname     IS '更新人（昵称）';
COMMENT ON COLUMN spider_site_urlregex.deleted              IS '是否删除（默认否）';
COMMENT ON COLUMN spider_site_urlregex.remark               IS '备注';

-- 爬虫-站点-Header
DROP TABLE IF EXISTS spider_site_header;
CREATE TABLE spider_site_header(
    header_id                 	CHAR(32) 		PRIMARY KEY,
    spider_id					CHAR(19)		NOT NULL,
    site_id						CHAR(19)		NOT NULL,
    name						VARCHAR(255),
    val							VARCHAR(1024),
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32),
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
CREATE INDEX IdxSpiderSiteHeader_site_id ON spider_site_header(site_id);
COMMENT ON TABLE spider_site_header 						IS '爬虫-站点-Header';
COMMENT ON COLUMN spider_site_header.header_id          	IS 'HeaderId';
COMMENT ON COLUMN spider_site_header.spider_id       	    IS '爬虫ID';
COMMENT ON COLUMN spider_site_header.site_id       			IS '站点ID';
COMMENT ON COLUMN spider_site_header.name   				IS 'HeaderName';
COMMENT ON COLUMN spider_site_header.val   					IS 'HeaderValue';
COMMENT ON COLUMN spider_site_header.created_at           	IS '创建时间';
COMMENT ON COLUMN spider_site_header.creator_user_id        IS '创建人（用户ID）';
COMMENT ON COLUMN spider_site_header.creator_nickname       IS '创建人（昵称）';
COMMENT ON COLUMN spider_site_header.updated_at           	IS '更新时间';
COMMENT ON COLUMN spider_site_header.updator_user_id        IS '更新人（用户ID）';
COMMENT ON COLUMN spider_site_header.updator_nickname       IS '更新人（昵称）';
COMMENT ON COLUMN spider_site_header.deleted              	IS '是否删除（默认否）';
COMMENT ON COLUMN spider_site_header.remark               	IS '备注';

-- 爬虫-站点-Cookie
DROP TABLE IF EXISTS spider_site_cookie;
CREATE TABLE spider_site_cookie(
    cookie_id                 	CHAR(32) 		PRIMARY KEY,
    spider_id					CHAR(19)		NOT NULL,
    site_id						CHAR(19)		NOT NULL,
    domain                 		VARCHAR(255),
    name						VARCHAR(255),
    val							VARCHAR(255),
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32),
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
CREATE INDEX IdxSpiderSiteCookie_site_id ON spider_site_cookie(site_id);
COMMENT ON TABLE spider_site_cookie 						IS '爬虫-站点-Cookie';
COMMENT ON COLUMN spider_site_cookie.cookie_id          	IS 'CookieId';
COMMENT ON COLUMN spider_site_cookie.spider_id       	    IS '爬虫ID';
COMMENT ON COLUMN spider_site_cookie.site_id       			IS '站点ID';
COMMENT ON COLUMN spider_site_cookie.domain                 IS '域名（为空时指定全局Cookie）';
COMMENT ON COLUMN spider_site_cookie.name   				IS 'CookieName';
COMMENT ON COLUMN spider_site_cookie.val   					IS 'CookieValue';
COMMENT ON COLUMN spider_site_cookie.created_at           	IS '创建时间';
COMMENT ON COLUMN spider_site_cookie.creator_user_id        IS '创建人（用户ID）';
COMMENT ON COLUMN spider_site_cookie.creator_nickname       IS '创建人（昵称）';
COMMENT ON COLUMN spider_site_cookie.updated_at           	IS '更新时间';
COMMENT ON COLUMN spider_site_cookie.updator_user_id        IS '更新人（用户ID）';
COMMENT ON COLUMN spider_site_cookie.updator_nickname       IS '更新人（昵称）';
COMMENT ON COLUMN spider_site_cookie.deleted              	IS '是否删除（默认否）';
COMMENT ON COLUMN spider_site_cookie.remark               	IS '备注';

-- 爬虫-站点-抓取链接
DROP TABLE IF EXISTS spider_site_graburl;
CREATE TABLE spider_site_graburl(
    graburl_id                 	CHAR(32) 		PRIMARY KEY,
    spider_id					VARCHAR(19)		NOT NULL,
    site_id						VARCHAR(19)		NOT NULL,
    site_name       			VARCHAR(255)	NOT NULL,
    starturl_id					VARCHAR(32),
	urlregex_id					VARCHAR(32),
    page_url_type				INT,
    depth						INT,
    flag						VARCHAR(255),
    url							VARCHAR(500),
    referer						VARCHAR(500),
    status_code					INT,
    html_title					VARCHAR(255),
    html_content				TEXT,
    screenshot_base64			TEXT,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     				TIMESTAMP,
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
CREATE INDEX IdxSpiderSiteGraburl_spider_id ON spider_site_graburl(spider_id);
CREATE INDEX IdxSpiderSiteGraburl_site_id ON spider_site_graburl(site_id);
CREATE INDEX IdxSpiderSiteGraburl_starturl_id ON spider_site_graburl(starturl_id);
CREATE INDEX IdxSpiderSiteGraburl_urlregex_id ON spider_site_graburl(urlregex_id);
CREATE INDEX IdxSpiderSiteGraburl_created_at ON spider_site_graburl(created_at);
COMMENT ON TABLE spider_site_graburl 						IS '爬虫-站点-抓取链接';
COMMENT ON COLUMN spider_site_graburl.graburl_id          	IS '抓取链接ID';
COMMENT ON COLUMN spider_site_graburl.spider_id       		IS '爬虫ID';
COMMENT ON COLUMN spider_site_graburl.site_id       		IS '站点ID';
COMMENT ON COLUMN spider_site_graburl.site_name       		IS '站点名称';
COMMENT ON COLUMN spider_site_graburl.starturl_id       	IS '入口链接ID';
COMMENT ON COLUMN spider_site_graburl.urlregex_id          	IS 'Url匹配正则ID';
COMMENT ON COLUMN spider_site_graburl.page_url_type       	IS '页面链接类型，-1：入口链接，0：种子，1：数据';
COMMENT ON COLUMN spider_site_graburl.depth       			IS '爬取深度';
COMMENT ON COLUMN spider_site_graburl.flag       			IS '标记值';
COMMENT ON COLUMN spider_site_graburl.url       			IS '页面链接';
COMMENT ON COLUMN spider_site_graburl.referer       		IS '访问来源';
COMMENT ON COLUMN spider_site_graburl.status_code       	IS '请求响应码';
COMMENT ON COLUMN spider_site_graburl.html_title       		IS '网页Title';
COMMENT ON COLUMN spider_site_graburl.html_content       	IS '网页内容';
COMMENT ON COLUMN spider_site_graburl.screenshot_base64     IS '网页截屏图片（Base64）';
COMMENT ON COLUMN spider_site_graburl.created_at           	IS '创建时间';
COMMENT ON COLUMN spider_site_graburl.updated_at           	IS '更新时间';
COMMENT ON COLUMN spider_site_graburl.deleted              	IS '是否删除（默认否）';
COMMENT ON COLUMN spider_site_graburl.remark               	IS '备注';

-- 爬虫数据-公示列表
DROP TABLE IF EXISTS crawl_announce;
CREATE TABLE crawl_announce(
    announce_id				VARCHAR(1024)	PRIMARY KEY,
    spider_id				VARCHAR(19)		NOT NULL,
    site_id					VARCHAR(19)		NOT NULL,
    site_name       		VARCHAR(255)	NOT NULL,
    starturl_id				VARCHAR(32),
    urlregex_id				VARCHAR(32),
    graburl_id				VARCHAR(32),
    name					TEXT,
    announce_category		VARCHAR(255),
    announce_type			VARCHAR(255),
    trade					VARCHAR(255),	
    release_date     		DATE,
    info_url				VARCHAR(255),
    flag					INT,
    status					INT				DEFAULT 0,
    times					INT				DEFAULT 1,
    message					TEXT,
    created_at            	TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     			TIMESTAMP,
    deleted                	BOOLEAN 		DEFAULT FALSE,
    remark               	VARCHAR(800)
)WITH(OIDS=FALSE);
CREATE INDEX IdxCrawlAnnounce_spider_id ON crawl_announce(spider_id);
CREATE INDEX IdxCrawlAnnounce_site_id ON crawl_announce(site_id);
CREATE INDEX IdxCrawlAnnounce_starturl_id ON crawl_announce(starturl_id);
CREATE INDEX IdxCrawlAnnounce_urlregex_id ON crawl_announce(urlregex_id);
CREATE INDEX IdxCrawlAnnounce_graburl_id ON crawl_announce(graburl_id);
CREATE INDEX IdxCrawlAnnounce_announce_category ON crawl_announce(announce_category);
CREATE INDEX IdxCrawlAnnounce_announce_type ON crawl_announce(announce_type);
CREATE INDEX IdxCrawlAnnounce_release_date ON crawl_announce(release_date);
CREATE INDEX IdxCrawlAnnounce_flag ON crawl_announce(flag);
CREATE INDEX IdxCrawlAnnounce_status ON crawl_announce(status);
COMMENT ON TABLE crawl_announce 							IS '爬虫数据-公示列表';
COMMENT ON COLUMN crawl_announce.announce_id       			IS '公示ID';
COMMENT ON COLUMN crawl_announce.spider_id       			IS '爬虫ID';
COMMENT ON COLUMN crawl_announce.site_id               		IS '站点ID';
COMMENT ON COLUMN crawl_announce.site_name       			IS '站点名称';
COMMENT ON COLUMN crawl_announce.starturl_id               	IS '入口链接ID';
COMMENT ON COLUMN crawl_announce.urlregex_id               	IS 'Url匹配正则ID';
COMMENT ON COLUMN crawl_announce.graburl_id               	IS '抓取链接ID';
COMMENT ON COLUMN crawl_announce.name               		IS '公示名称';
COMMENT ON COLUMN crawl_announce.announce_category          IS '公示类别';
COMMENT ON COLUMN crawl_announce.announce_type              IS '公告类型';
COMMENT ON COLUMN crawl_announce.trade 						IS '交易中心';
COMMENT ON COLUMN crawl_announce.release_date				IS '发布日期';
COMMENT ON COLUMN crawl_announce.info_url            		IS '详情网页链接';
COMMENT ON COLUMN crawl_announce.flag            			IS '标记，-1：废弃（爬虫不再维护），0：抓取异常，1：抓取成功，2：人工修正（爬虫不再维护）';
COMMENT ON COLUMN crawl_announce.status            			IS '解析状态，-1：解析失败，0：未解析，1：解析成功';
COMMENT ON COLUMN crawl_announce.times            			IS '爬取次数（默认1）';
COMMENT ON COLUMN crawl_announce.message            		IS '消息内容（例：爬取异常）';
COMMENT ON COLUMN crawl_announce.created_at               	IS '创建时间';
COMMENT ON COLUMN crawl_announce.updated_at               	IS '更新时间';
COMMENT ON COLUMN crawl_announce.deleted                  	IS '是否删除（默认否）';
COMMENT ON COLUMN crawl_announce.remark               		IS '备注';

-- 爬虫数据-公示详情-采购公告
DROP TABLE IF EXISTS crawl_purchase_notice;
CREATE TABLE crawl_purchase_notice(
    purchase_notice_id      		VARCHAR(1024) 	PRIMARY KEY,
    announce_id        				VARCHAR(1024),
    spider_id						VARCHAR(19)		NOT NULL,
    site_id							VARCHAR(19)		NOT NULL,
    site_name       				VARCHAR(255)	NOT NULL,
    starturl_id						VARCHAR(32),
    urlregex_id						VARCHAR(32),
    graburl_id						VARCHAR(32),
    name							TEXT,
    announce_category				VARCHAR(255),
    announce_type					VARCHAR(255),
    release_date     				DATE,
    info_url						VARCHAR(255),
    status							INT				DEFAULT 1,
    times							INT				DEFAULT 1,
    project_no						VARCHAR(255),
    project_name					VARCHAR(800),
    budget_amount					DECIMAL(15, 2),
    budget_amount_str				VARCHAR(255),
    purchase_name					VARCHAR(800),
    purchase_address				VARCHAR(800),
    purchase_contact				VARCHAR(255),
    purchase_agency_name			VARCHAR(800),
    purchase_agency_address			VARCHAR(800),
    purchase_agency_contact			VARCHAR(255),
    project_contact_name			VARCHAR(255),
    project_contact_phone			VARCHAR(255),
    source							VARCHAR(255),
    created_at            			TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     					TIMESTAMP,
    deleted                			BOOLEAN 		DEFAULT FALSE
)WITH(OIDS=FALSE);
CREATE INDEX IdxCrawlPurchaseNotice_announce_id ON crawl_purchase_notice(announce_id);
CREATE INDEX IdxCrawlPurchaseNotice_graburl_id ON crawl_purchase_notice(graburl_id);
CREATE INDEX IdxCrawlPurchaseNotice_site_id ON crawl_purchase_notice(site_id);
CREATE INDEX IdxCrawlPurchaseNotice_info_url ON crawl_purchase_notice(info_url);
CREATE INDEX IdxCrawlPurchaseNotice_announce_category ON crawl_purchase_notice(announce_category);
CREATE INDEX IdxCrawlPurchaseNotice_announce_type ON crawl_purchase_notice(announce_type);
CREATE INDEX IdxCrawlPurchaseNotice_release_date ON crawl_purchase_notice(release_date);
CREATE INDEX IdxCrawlPurchaseNotice_status ON crawl_purchase_notice(status);
COMMENT ON TABLE crawl_purchase_notice 									IS '爬虫数据-公示详情-采购公告';
COMMENT ON COLUMN crawl_purchase_notice.purchase_notice_id  			IS '采购公告ID';
COMMENT ON COLUMN crawl_purchase_notice.announce_id    					IS '公示ID';
COMMENT ON COLUMN crawl_purchase_notice.spider_id       				IS '爬虫ID';
COMMENT ON COLUMN crawl_purchase_notice.site_id               			IS '站点ID';
COMMENT ON COLUMN crawl_purchase_notice.site_name       				IS '站点名称';
COMMENT ON COLUMN crawl_purchase_notice.starturl_id               		IS '入口链接ID';
COMMENT ON COLUMN crawl_purchase_notice.urlregex_id               		IS 'Url匹配正则ID';
COMMENT ON COLUMN crawl_purchase_notice.graburl_id               		IS '抓取链接ID';
COMMENT ON COLUMN crawl_purchase_notice.name               				IS '公示名称';
COMMENT ON COLUMN crawl_purchase_notice.announce_category    			IS '公示类别';
COMMENT ON COLUMN crawl_purchase_notice.announce_type   				IS '公告类型';
COMMENT ON COLUMN crawl_purchase_notice.release_date					IS '发布日期';
COMMENT ON COLUMN crawl_purchase_notice.info_url          				IS '详情页链接';
COMMENT ON COLUMN crawl_purchase_notice.status            				IS '解析状态，-1：解析失败，0：未解析，1：解析成功';
COMMENT ON COLUMN crawl_purchase_notice.times            				IS '解析次数（默认1）';
COMMENT ON COLUMN crawl_purchase_notice.project_no						IS '项目编号';
COMMENT ON COLUMN crawl_purchase_notice.project_name					IS '项目名称';
COMMENT ON COLUMN crawl_purchase_notice.budget_amount					IS '预算金额（元）';
COMMENT ON COLUMN crawl_purchase_notice.budget_amount_str				IS '预算金额（文本）';
COMMENT ON COLUMN crawl_purchase_notice.purchase_name					IS '采购人信息-名称';
COMMENT ON COLUMN crawl_purchase_notice.purchase_address				IS '采购人信息-地址';
COMMENT ON COLUMN crawl_purchase_notice.purchase_contact				IS '采购人信息-联系方式';
COMMENT ON COLUMN crawl_purchase_notice.purchase_agency_name			IS '采购代理机构信息-名称';
COMMENT ON COLUMN crawl_purchase_notice.purchase_agency_address			IS '采购代理机构信息-地址';
COMMENT ON COLUMN crawl_purchase_notice.purchase_agency_contact			IS '采购代理机构信息-联系方式';
COMMENT ON COLUMN crawl_purchase_notice.project_contact_name			IS '项目联系方式-项目联系人';
COMMENT ON COLUMN crawl_purchase_notice.project_contact_phone			IS '项目联系方式-电话';
COMMENT ON COLUMN crawl_purchase_notice.source							IS '来源';
COMMENT ON COLUMN crawl_purchase_notice.created_at          			IS '创建时间';
COMMENT ON COLUMN crawl_purchase_notice.updated_at          			IS '更新时间';
COMMENT ON COLUMN crawl_purchase_notice.deleted             			IS '是否删除（默认否）';

-- 爬虫数据-公示详情-采购结果公告
DROP TABLE IF EXISTS crawl_purchase_result;
CREATE TABLE crawl_purchase_result(
    purchase_result_id      		VARCHAR(1024) 	PRIMARY KEY,
    announce_id        				VARCHAR(1024),
    spider_id						VARCHAR(19)		NOT NULL,
    site_id							VARCHAR(19)		NOT NULL,
    site_name       				VARCHAR(255)	NOT NULL,
    starturl_id						VARCHAR(32),
    urlregex_id						VARCHAR(32),
    graburl_id						VARCHAR(32),
    name							TEXT,
    announce_category				VARCHAR(255),
    announce_type					VARCHAR(255),
    release_date     				DATE,
    info_url						VARCHAR(255),
    status							INT				DEFAULT 1,
    times							INT				DEFAULT 1,
    project_no						VARCHAR(255),
    project_name					VARCHAR(800),
    seq								VARCHAR(255),
    item_name						VARCHAR(800),
    total_price						DECIMAL(15, 2),
    supplier_name					VARCHAR(800),
    supplier_address				VARCHAR(800),
    supplier_social_credit			VARCHAR(255),
    purchase_name					VARCHAR(800),
    purchase_address				VARCHAR(800),
    purchase_fax					VARCHAR(255),
    purchase_contact_name			VARCHAR(255),
    purchase_contact_phone			VARCHAR(255),
    purchase_agency_name			VARCHAR(800),
    purchase_agency_address			VARCHAR(800),
    purchase_agency_fax				VARCHAR(255),
    purchase_agency_contact_name	VARCHAR(255),
    purchase_agency_contact_phone	VARCHAR(255),
    source							VARCHAR(255),
    created_at            			TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     					TIMESTAMP,
    deleted                			BOOLEAN 		DEFAULT FALSE
)WITH(OIDS=FALSE);
CREATE INDEX IdxCrawlPurchaseResult_announce_id ON crawl_purchase_result(announce_id);
CREATE INDEX IdxCrawlPurchaseResult_graburl_id ON crawl_purchase_result(graburl_id);
CREATE INDEX IdxCrawlPurchaseResult_site_id ON crawl_purchase_result(site_id);
CREATE INDEX IdxCrawlPurchaseResult_info_url ON crawl_purchase_result(info_url);
CREATE INDEX IdxCrawlPurchaseResult_announce_category ON crawl_purchase_result(announce_category);
CREATE INDEX IdxCrawlPurchaseResult_announce_type ON crawl_purchase_result(announce_type);
CREATE INDEX IdxCrawlPurchaseResult_release_date ON crawl_purchase_result(release_date);
CREATE INDEX IdxCrawlPurchaseResult_status ON crawl_purchase_result(status);
COMMENT ON TABLE crawl_purchase_result 									IS '爬虫数据-公示详情-采购结果公告';
COMMENT ON COLUMN crawl_purchase_result.purchase_result_id  			IS '中标结果ID（公示ID+序号）';
COMMENT ON COLUMN crawl_purchase_result.announce_id    					IS '公示ID';
COMMENT ON COLUMN crawl_purchase_result.spider_id       				IS '爬虫ID';
COMMENT ON COLUMN crawl_purchase_result.site_id               			IS '站点ID';
COMMENT ON COLUMN crawl_purchase_result.site_name       				IS '站点名称';
COMMENT ON COLUMN crawl_purchase_result.starturl_id               		IS '入口链接ID';
COMMENT ON COLUMN crawl_purchase_result.urlregex_id               		IS 'Url匹配正则ID';
COMMENT ON COLUMN crawl_purchase_result.graburl_id               		IS '抓取链接ID';
COMMENT ON COLUMN crawl_purchase_result.name               				IS '公示名称';
COMMENT ON COLUMN crawl_purchase_result.announce_category    			IS '公示类别';
COMMENT ON COLUMN crawl_purchase_result.announce_type   				IS '公告类型';
COMMENT ON COLUMN crawl_purchase_result.release_date					IS '发布日期';
COMMENT ON COLUMN crawl_purchase_result.info_url          				IS '详情页链接';
COMMENT ON COLUMN crawl_purchase_result.status            				IS '解析状态，-1：解析失败，0：未解析，1：解析成功';
COMMENT ON COLUMN crawl_purchase_result.times            				IS '解析次数（默认1）';
COMMENT ON COLUMN crawl_purchase_result.project_no						IS '项目编号';
COMMENT ON COLUMN crawl_purchase_result.project_name					IS '项目名称';
COMMENT ON COLUMN crawl_purchase_result.seq								IS '序号';
COMMENT ON COLUMN crawl_purchase_result.item_name						IS '标项名称';
COMMENT ON COLUMN crawl_purchase_result.total_price						IS '总价（元）';
COMMENT ON COLUMN crawl_purchase_result.supplier_name					IS '中标供应商名称';
COMMENT ON COLUMN crawl_purchase_result.supplier_address				IS '中标供应商地址';
COMMENT ON COLUMN crawl_purchase_result.supplier_social_credit			IS '中标供应商统一社会信用代码';
COMMENT ON COLUMN crawl_purchase_result.purchase_name					IS '采购人-名称';
COMMENT ON COLUMN crawl_purchase_result.purchase_address				IS '采购人-地址';
COMMENT ON COLUMN crawl_purchase_result.purchase_fax					IS '采购人-传真';
COMMENT ON COLUMN crawl_purchase_result.purchase_contact_name			IS '采购人-项目联系人';
COMMENT ON COLUMN crawl_purchase_result.purchase_contact_phone			IS '采购人-项目联系方式';
COMMENT ON COLUMN crawl_purchase_result.purchase_agency_name			IS '采购代理机构-名称';
COMMENT ON COLUMN crawl_purchase_result.purchase_agency_address			IS '采购代理机构-地址';
COMMENT ON COLUMN crawl_purchase_result.purchase_agency_fax				IS '采购代理机构-传真';
COMMENT ON COLUMN crawl_purchase_result.purchase_agency_contact_name	IS '采购代理机构-项目联系人';
COMMENT ON COLUMN crawl_purchase_result.purchase_agency_contact_phone	IS '采购代理机构-项目联系方式';
COMMENT ON COLUMN crawl_purchase_result.source							IS '来源';
COMMENT ON COLUMN crawl_purchase_result.created_at          			IS '创建时间';
COMMENT ON COLUMN crawl_purchase_result.updated_at          			IS '更新时间';
COMMENT ON COLUMN crawl_purchase_result.deleted             			IS '是否删除（默认否）';

-- 爬虫数据-公示详情-招标公告
DROP TABLE IF EXISTS crawl_bid_notice;
CREATE TABLE crawl_bid_notice(
    bid_notice_id      				VARCHAR(1024) 	PRIMARY KEY,
    announce_id        				VARCHAR(1024),
    spider_id						VARCHAR(19)		NOT NULL,
    site_id							VARCHAR(19)		NOT NULL,
    site_name       				VARCHAR(255)	NOT NULL,
    starturl_id						VARCHAR(32),
    urlregex_id						VARCHAR(32),
    graburl_id						VARCHAR(32),
    name							TEXT,
    announce_category				VARCHAR(255),
    announce_type					VARCHAR(255),
    release_date     				DATE,
    info_url						VARCHAR(255),
    status							INT				DEFAULT 1,
    times							INT				DEFAULT 1,
    project_name					VARCHAR(800),
    tenderee_name					VARCHAR(800),
    tenderee_address				VARCHAR(800),
    tenderee_postal					VARCHAR(255),
    tenderee_contact_name			VARCHAR(255),
    tenderee_contact_phone			VARCHAR(255),
    tenderee_contact_mobile			VARCHAR(255),
    tenderee_fax					VARCHAR(255),
    tenderee_bank_name				VARCHAR(800),
    tenderee_bank_account			VARCHAR(255),
    tenderee_agent_name				VARCHAR(800),
    tenderee_agent_address			VARCHAR(800),
    tenderee_agent_postal			VARCHAR(255),
    tenderee_agent_contact_name		VARCHAR(255),
    tenderee_agent_contact_phone	VARCHAR(255),
    tenderee_agent_contact_mobile	VARCHAR(255),
    tenderee_agent_fax				VARCHAR(255),
    tenderee_agent_bank_name		VARCHAR(800),
    tenderee_agent_bank_account		VARCHAR(255),
    source							VARCHAR(255),
    created_at            			TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     					TIMESTAMP,
    deleted                			BOOLEAN 		DEFAULT FALSE
)WITH(OIDS=FALSE);
CREATE INDEX IdxCrawlBidNotice_announce_id ON crawl_bid_notice(announce_id);
CREATE INDEX IdxCrawlBidNotice_graburl_id ON crawl_bid_notice(graburl_id);
CREATE INDEX IdxCrawlBidNotice_site_id ON crawl_bid_notice(site_id);
CREATE INDEX IdxCrawlBidNotice_info_url ON crawl_bid_notice(info_url);
CREATE INDEX IdxCrawlBidNotice_announce_category ON crawl_bid_notice(announce_category);
CREATE INDEX IdxCrawlBidNotice_announce_type ON crawl_bid_notice(announce_type);
CREATE INDEX IdxCrawlBidNotice_release_date ON crawl_bid_notice(release_date);
CREATE INDEX IdxCrawlBidNotice_status ON crawl_bid_notice(status);
COMMENT ON TABLE crawl_bid_notice 										IS '爬虫数据-公示详情-招标公告';
COMMENT ON COLUMN crawl_bid_notice.bid_notice_id  						IS '招标公告ID';
COMMENT ON COLUMN crawl_bid_notice.announce_id    						IS '公示ID';
COMMENT ON COLUMN crawl_bid_notice.spider_id       						IS '爬虫ID';
COMMENT ON COLUMN crawl_bid_notice.site_id               				IS '站点ID';
COMMENT ON COLUMN crawl_bid_notice.site_name       						IS '站点名称';
COMMENT ON COLUMN crawl_bid_notice.starturl_id               			IS '入口链接ID';
COMMENT ON COLUMN crawl_bid_notice.urlregex_id               			IS 'Url匹配正则ID';
COMMENT ON COLUMN crawl_bid_notice.graburl_id               			IS '抓取链接ID';
COMMENT ON COLUMN crawl_bid_notice.name               					IS '公示名称';
COMMENT ON COLUMN crawl_bid_notice.announce_category    				IS '公示类别';
COMMENT ON COLUMN crawl_bid_notice.announce_type   						IS '公告类型';
COMMENT ON COLUMN crawl_bid_notice.release_date							IS '发布日期';
COMMENT ON COLUMN crawl_bid_notice.info_url          					IS '详情页链接';
COMMENT ON COLUMN crawl_bid_notice.status            					IS '解析状态，-1：解析失败，0：未解析，1：解析成功';
COMMENT ON COLUMN crawl_bid_notice.times            					IS '解析次数（默认1）';
COMMENT ON COLUMN crawl_bid_notice.project_name							IS '项目名称';
COMMENT ON COLUMN crawl_bid_notice.tenderee_name						IS '招标人-名称';
COMMENT ON COLUMN crawl_bid_notice.tenderee_address						IS '招标人-地址';
COMMENT ON COLUMN crawl_bid_notice.tenderee_postal						IS '招标人-邮编';
COMMENT ON COLUMN crawl_bid_notice.tenderee_contact_name				IS '招标人-联系人';
COMMENT ON COLUMN crawl_bid_notice.tenderee_contact_phone				IS '招标人-电话';
COMMENT ON COLUMN crawl_bid_notice.tenderee_contact_mobile				IS '招标人-手机号码';
COMMENT ON COLUMN crawl_bid_notice.tenderee_fax							IS '招标人-传真';
COMMENT ON COLUMN crawl_bid_notice.tenderee_bank_name					IS '招标人-开户银行';
COMMENT ON COLUMN crawl_bid_notice.tenderee_bank_account				IS '招标人-银行账号';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_name					IS '招标代理机构-名称';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_address				IS '招标代理机构-地址';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_postal				IS '招标代理机构-邮编';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_contact_name			IS '招标代理机构-联系人';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_contact_phone			IS '招标代理机构-电话';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_contact_mobile		IS '招标代理机构-手机号码';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_fax					IS '招标代理机构-传真';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_bank_name				IS '招标代理机构-开户银行';
COMMENT ON COLUMN crawl_bid_notice.tenderee_agent_bank_account			IS '招标代理机构-银行账号';
COMMENT ON COLUMN crawl_bid_notice.source								IS '来源';
COMMENT ON COLUMN crawl_bid_notice.created_at          					IS '创建时间';
COMMENT ON COLUMN crawl_bid_notice.updated_at          					IS '更新时间';
COMMENT ON COLUMN crawl_bid_notice.deleted             					IS '是否删除（默认否）';

-- 爬虫数据-公示详情-中标候选人公示
DROP TABLE IF EXISTS crawl_bid_candidate;
CREATE TABLE crawl_bid_candidate(
    bid_candidate_id      			VARCHAR(1024) 	PRIMARY KEY,
    announce_id        				VARCHAR(1024),
    spider_id						VARCHAR(19)		NOT NULL,
    site_id							VARCHAR(19)		NOT NULL,
    site_name       				VARCHAR(255)	NOT NULL,
    starturl_id						VARCHAR(32),
    urlregex_id						VARCHAR(32),
    graburl_id						VARCHAR(32),
    name							TEXT,
    announce_category				VARCHAR(255),
    announce_type					VARCHAR(255),
    release_date     				DATE,
    info_url						VARCHAR(255),
    status							INT				DEFAULT 1,
    times							INT				DEFAULT 1,
    project_name					VARCHAR(800),
	lot_no							VARCHAR(255),
	lot_name						VARCHAR(800),
	bid_open_time					TIMESTAMP,
	bid_open_time_str				VARCHAR(255),
	bid_open_address				VARCHAR(255),
	rank							VARCHAR(255),
	candidate						VARCHAR(255),
	bid_price						DECIMAL(15, 2),
	bid_price_str					VARCHAR(255),
	score							VARCHAR(255),
	period							VARCHAR(255),
	project_leader					VARCHAR(255),
	project_leader_practice 		VARCHAR(255),
	certificate_no					VARCHAR(255),
	quality_standard				VARCHAR(255),
	category_grade					VARCHAR(255),
	supervision_name				VARCHAR(800),
	supervision_phone				VARCHAR(255),
	tenderee_name					VARCHAR(800),
	tenderee_contact_name			VARCHAR(255),
	tenderee_contact_phone			VARCHAR(255),
	tenderee_address				VARCHAR(800),
	tenderee_agent_name				VARCHAR(800),
	tenderee_agent_contact_name		VARCHAR(255),
	tenderee_agent_contact_phone	VARCHAR(255),
	tenderee_agent_address			VARCHAR(800),
    source							VARCHAR(255),
    created_at            			TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     					TIMESTAMP,
    deleted                			BOOLEAN 		DEFAULT FALSE
)WITH(OIDS=FALSE);
CREATE INDEX IdxCrawlBidCandidate_announce_id ON crawl_bid_candidate(announce_id);
CREATE INDEX IdxCrawlBidCandidate_graburl_id ON crawl_bid_candidate(graburl_id);
CREATE INDEX IdxCrawlBidCandidate_site_id ON crawl_bid_candidate(site_id);
CREATE INDEX IdxCrawlBidCandidate_info_url ON crawl_bid_candidate(info_url);
CREATE INDEX IdxCrawlBidCandidate_announce_category ON crawl_bid_candidate(announce_category);
CREATE INDEX IdxCrawlBidCandidate_announce_type ON crawl_bid_candidate(announce_type);
CREATE INDEX IdxCrawlBidCandidate_release_date ON crawl_bid_candidate(release_date);
CREATE INDEX IdxCrawlBidCandidate_status ON crawl_bid_candidate(status);
COMMENT ON TABLE crawl_bid_candidate 									IS '爬虫数据-公示详情-中标候选人公示';
COMMENT ON COLUMN crawl_bid_candidate.bid_candidate_id  				IS '中标候选人公示ID';
COMMENT ON COLUMN crawl_bid_candidate.announce_id    					IS '公示ID';
COMMENT ON COLUMN crawl_bid_candidate.spider_id       					IS '爬虫ID';
COMMENT ON COLUMN crawl_bid_candidate.site_id               			IS '站点ID';
COMMENT ON COLUMN crawl_bid_candidate.site_name       					IS '站点名称';
COMMENT ON COLUMN crawl_bid_candidate.starturl_id               		IS '入口链接ID';
COMMENT ON COLUMN crawl_bid_candidate.urlregex_id               		IS 'Url匹配正则ID';
COMMENT ON COLUMN crawl_bid_candidate.graburl_id               			IS '抓取链接ID';
COMMENT ON COLUMN crawl_bid_candidate.name               				IS '公示名称';
COMMENT ON COLUMN crawl_bid_candidate.announce_category    				IS '公示类别';
COMMENT ON COLUMN crawl_bid_candidate.announce_type   					IS '公告类型';
COMMENT ON COLUMN crawl_bid_candidate.release_date						IS '发布日期';
COMMENT ON COLUMN crawl_bid_candidate.info_url          				IS '详情页链接';
COMMENT ON COLUMN crawl_bid_candidate.status            				IS '解析状态，-1：解析失败，0：未解析，1：解析成功';
COMMENT ON COLUMN crawl_bid_candidate.times            					IS '解析次数（默认1）';
COMMENT ON COLUMN crawl_bid_candidate.project_name						IS '项目名称';
COMMENT ON COLUMN crawl_bid_candidate.lot_no							IS '标段(包)编号';
COMMENT ON COLUMN crawl_bid_candidate.lot_name							IS '标段(包)名称';
COMMENT ON COLUMN crawl_bid_candidate.bid_open_time						IS '开标时间';
COMMENT ON COLUMN crawl_bid_candidate.bid_open_time_str					IS '开标时间（文本）';
COMMENT ON COLUMN crawl_bid_candidate.bid_open_address					IS '开标地点';
COMMENT ON COLUMN crawl_bid_candidate.rank								IS '中标候选人-位次';
COMMENT ON COLUMN crawl_bid_candidate.candidate							IS '中标候选人-名称';
COMMENT ON COLUMN crawl_bid_candidate.bid_price							IS '中标候选人-投标价（元）';
COMMENT ON COLUMN crawl_bid_candidate.bid_price_str						IS '中标候选人-投标价（文本）';
COMMENT ON COLUMN crawl_bid_candidate.score								IS '中标候选人-得分';
COMMENT ON COLUMN crawl_bid_candidate.period							IS '中标候选人-投标工期';
COMMENT ON COLUMN crawl_bid_candidate.project_leader					IS '中标候选人-项目负责人（项目经理）';
COMMENT ON COLUMN crawl_bid_candidate.project_leader_practice 			IS '中标候选人-项目负责人（项目经理）执业资格';
COMMENT ON COLUMN crawl_bid_candidate.certificate_no					IS '中标候选人-注册证号';
COMMENT ON COLUMN crawl_bid_candidate.quality_standard					IS '中标候选人-质量标准';
COMMENT ON COLUMN crawl_bid_candidate.category_grade					IS '中标候选人-资质类别和等级';
COMMENT ON COLUMN crawl_bid_candidate.supervision_name					IS '监督部门-名称';
COMMENT ON COLUMN crawl_bid_candidate.supervision_phone					IS '监督部门-联系电话';
COMMENT ON COLUMN crawl_bid_candidate.tenderee_name						IS '招标人-名称';
COMMENT ON COLUMN crawl_bid_candidate.tenderee_contact_name				IS '招标人-联系人';
COMMENT ON COLUMN crawl_bid_candidate.tenderee_contact_phone			IS '招标人-电话';
COMMENT ON COLUMN crawl_bid_candidate.tenderee_address					IS '招标人-地址';
COMMENT ON COLUMN crawl_bid_candidate.tenderee_agent_name				IS '招标代理机构-名称';
COMMENT ON COLUMN crawl_bid_candidate.tenderee_agent_contact_name		IS '招标代理机构-联系人';
COMMENT ON COLUMN crawl_bid_candidate.tenderee_agent_contact_phone		IS '招标代理机构-电话';
COMMENT ON COLUMN crawl_bid_candidate.tenderee_agent_address			IS '招标代理机构-地址';
COMMENT ON COLUMN crawl_bid_candidate.source							IS '来源';
COMMENT ON COLUMN crawl_bid_candidate.created_at          				IS '创建时间';
COMMENT ON COLUMN crawl_bid_candidate.updated_at          				IS '更新时间';
COMMENT ON COLUMN crawl_bid_candidate.deleted             				IS '是否删除（默认否）';

-- 爬虫数据-公示详情-中标结果公告
DROP TABLE IF EXISTS crawl_bid_result;
CREATE TABLE crawl_bid_result(
    bid_result_id      				VARCHAR(1024) 	PRIMARY KEY,
    announce_id        				VARCHAR(1024),
    spider_id						VARCHAR(19)		NOT NULL,
    site_id							VARCHAR(19)		NOT NULL,
    site_name       				VARCHAR(255)	NOT NULL,
    starturl_id						VARCHAR(32),
    urlregex_id						VARCHAR(32),
    graburl_id						VARCHAR(32),
    name							TEXT,
    announce_category				VARCHAR(255),
    announce_type					VARCHAR(255),
    release_date     				DATE,
    info_url						VARCHAR(255),
    status							INT				DEFAULT 1,
    times							INT				DEFAULT 1,
    project_no 						VARCHAR(255),
    project_name					VARCHAR(800),
    lot_no							VARCHAR(255),
    lot_name						VARCHAR(800),
    price							DECIMAL(15, 2),
    price_str						VARCHAR(255),
    bid_winner_name					VARCHAR(800),
    bid_winner_social_credit		VARCHAR(255),
    tenderee_name					VARCHAR(800),
    tenderee_contact				VARCHAR(255),
    source							VARCHAR(255),
    created_at            			TIMESTAMP 		NOT NULL DEFAULT NOW(),
    updated_at     					TIMESTAMP,
    deleted                			BOOLEAN 		DEFAULT FALSE
)WITH(OIDS=FALSE);
CREATE INDEX IdxCrawlBidResult_announce_id on crawl_bid_result(announce_id);
CREATE INDEX IdxCrawlBidResult_graburl_id on crawl_bid_result(graburl_id);
CREATE INDEX IdxCrawlBidResult_site_id on crawl_bid_result(site_id);
CREATE INDEX IdxCrawlBidResult_info_url on crawl_bid_result(info_url);
CREATE INDEX IdxCrawlBidResult_announce_category ON crawl_bid_result(announce_category);
CREATE INDEX IdxCrawlBidResult_announce_type ON crawl_bid_result(announce_type);
CREATE INDEX IdxCrawlBidResult_release_date ON crawl_bid_result(release_date);
CREATE INDEX IdxCrawlBidResult_status ON crawl_bid_result(status);
COMMENT ON TABLE crawl_bid_result 										IS '爬虫数据-公示详情-中标结果公告';
COMMENT ON COLUMN crawl_bid_result.bid_result_id  						IS '中标结果公告ID';
COMMENT ON COLUMN crawl_bid_result.announce_id    						IS '公示ID';
COMMENT ON COLUMN crawl_bid_result.spider_id       						IS '爬虫ID';
COMMENT ON COLUMN crawl_bid_result.site_id               				IS '站点ID';
COMMENT ON COLUMN crawl_bid_result.site_name       						IS '站点名称';
COMMENT ON COLUMN crawl_bid_result.starturl_id               			IS '入口链接ID';
COMMENT ON COLUMN crawl_bid_result.urlregex_id               			IS 'Url匹配正则ID';
COMMENT ON COLUMN crawl_bid_result.graburl_id               			IS '抓取链接ID';
COMMENT ON COLUMN crawl_bid_result.name               					IS '公示名称';
COMMENT ON COLUMN crawl_bid_result.announce_category    				IS '公示类别';
COMMENT ON COLUMN crawl_bid_result.announce_type   						IS '公告类型';
COMMENT ON COLUMN crawl_bid_result.release_date							IS '发布日期';
COMMENT ON COLUMN crawl_bid_result.info_url          					IS '详情页链接';
COMMENT ON COLUMN crawl_bid_result.status            					IS '解析状态，-1：解析失败，0：未解析，1：解析成功';
COMMENT ON COLUMN crawl_bid_result.times            					IS '解析次数（默认1）';
COMMENT ON COLUMN crawl_bid_result.project_no							IS '项目编号';
COMMENT ON COLUMN crawl_bid_result.project_name							IS '项目名称';
COMMENT ON COLUMN crawl_bid_result.lot_no								IS '标段编号';
COMMENT ON COLUMN crawl_bid_result.lot_name								IS '标段名称';
COMMENT ON COLUMN crawl_bid_result.price								IS '中标价（元）';
COMMENT ON COLUMN crawl_bid_result.price_str							IS '中标价（文本）';
COMMENT ON COLUMN crawl_bid_result.bid_winner_name						IS '中标单位-名称';
COMMENT ON COLUMN crawl_bid_result.bid_winner_social_credit				IS '中标单位-统一社会信用代码';
COMMENT ON COLUMN crawl_bid_result.tenderee_name						IS '招标人-名称';
COMMENT ON COLUMN crawl_bid_result.tenderee_contact						IS '招标人-联系方式';
COMMENT ON COLUMN crawl_bid_result.source								IS '来源';
COMMENT ON COLUMN crawl_bid_result.created_at          					IS '创建时间';
COMMENT ON COLUMN crawl_bid_result.updated_at          					IS '更新时间';
COMMENT ON COLUMN crawl_bid_result.deleted             					IS '是否删除（默认否）';

-- 数据源
DROP TABLE IF EXISTS ds;
CREATE TABLE ds(
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
CREATE TABLE er(
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
CREATE TABLE ds_er_rel(
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
CREATE TABLE er_field(
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

-- ER模型-字段库
DROP TABLE IF EXISTS er_field_lib;
CREATE TABLE er_field_lib(
    id                			BIGSERIAL 		PRIMARY KEY,
    uuid       					VARCHAR(32)		UNIQUE NOT NULL,
    name       					VARCHAR(255)	NOT NULL,
    comment       				VARCHAR(500),
    type       					INT				NOT NULL,
    type_name      				VARCHAR(255)	NOT NULL,
    size       					INT,
    digit       				INT,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
COMMENT ON TABLE er_field_lib 								IS 'ER模型-字段库';
COMMENT ON COLUMN er_field_lib.id                			IS '主键ID';
COMMENT ON COLUMN er_field_lib.uuid       					IS 'UUID';
COMMENT ON COLUMN er_field_lib.name       					IS '列名';
COMMENT ON COLUMN er_field_lib.comment       				IS '注释';
COMMENT ON COLUMN er_field_lib.type       					IS '类型，java.sql.Types';
COMMENT ON COLUMN er_field_lib.type_name       				IS '类型名称';
COMMENT ON COLUMN er_field_lib.size       					IS '精度';
COMMENT ON COLUMN er_field_lib.digit       					IS '标度';
COMMENT ON COLUMN er_field_lib.created_at                   IS '创建时间';
COMMENT ON COLUMN er_field_lib.creator_user_id            	IS '创建人（用户ID）';
COMMENT ON COLUMN er_field_lib.creator_nickname             IS '创建人（昵称）';
COMMENT ON COLUMN er_field_lib.updated_at               	IS '更新时间';
COMMENT ON COLUMN er_field_lib.updator_user_id            	IS '更新人（用户ID）';
COMMENT ON COLUMN er_field_lib.updator_nickname           	IS '更新人（昵称）';
COMMENT ON COLUMN er_field_lib.deleted                    	IS '是否删除（默认否）';
COMMENT ON COLUMN er_field_lib.remark                      	IS '备注';

-- 图形设计-图库组
DROP TABLE IF EXISTS mxgraph_group;
CREATE TABLE mxgraph_group(
    id                			BIGSERIAL 		PRIMARY KEY,
    uuid       					VARCHAR(32)		UNIQUE NOT NULL,
    text						VARCHAR(255)	NOT NULL,
    icon_cls					VARCHAR(255),
    level						INT				NOT NULL,
    sort						INT				NOT NULL,
    state						VARCHAR(20)		NOT NULL,
    pid							BIGINT,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE,
    remark               		VARCHAR(800)
)WITH(OIDS=FALSE);
COMMENT ON TABLE mxgraph_group 								IS '图形设计-图库组';
COMMENT ON COLUMN mxgraph_group.id                			IS '主键ID';
COMMENT ON COLUMN mxgraph_group.uuid       					IS 'UUID';
COMMENT ON COLUMN mxgraph_group.text       					IS '名称';
COMMENT ON COLUMN mxgraph_group.icon_cls       				IS '图标';
COMMENT ON COLUMN mxgraph_group.level       				IS '层级';
COMMENT ON COLUMN mxgraph_group.sort       					IS '排序';
COMMENT ON COLUMN mxgraph_group.state       				IS '展开状态，open：无子角色、closed：有子角色';
COMMENT ON COLUMN mxgraph_group.pid       					IS '父级id';
COMMENT ON COLUMN mxgraph_group.created_at                  IS '创建时间';
COMMENT ON COLUMN mxgraph_group.creator_user_id            	IS '创建人（用户ID）';
COMMENT ON COLUMN mxgraph_group.creator_nickname            IS '创建人（昵称）';
COMMENT ON COLUMN mxgraph_group.updated_at               	IS '更新时间';
COMMENT ON COLUMN mxgraph_group.updator_user_id            	IS '更新人（用户ID）';
COMMENT ON COLUMN mxgraph_group.updator_nickname           	IS '更新人（昵称）';
COMMENT ON COLUMN mxgraph_group.deleted                    	IS '是否删除（默认否）';
COMMENT ON COLUMN mxgraph_group.remark                      IS '备注';

-- 图形设计
DROP TABLE IF EXISTS mxgraph;
CREATE TABLE mxgraph(
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

-- 图形设计-图库组关系
DROP TABLE IF EXISTS mxgraph_group_rel;
CREATE TABLE mxgraph_group_rel(
    id                			BIGSERIAL 		PRIMARY KEY,
    mxgraph_group_id			BIGINT			NOT NULL,
    mxgraph_id					BIGINT			NOT NULL,
    created_at            		TIMESTAMP 		NOT NULL DEFAULT NOW(),
    creator_user_id             VARCHAR(32)		NOT NULL,
    creator_nickname           	VARCHAR(255),
    updated_at     				TIMESTAMP,
    updator_user_id             VARCHAR(32),
    updator_nickname           	VARCHAR(255),
    deleted                		BOOLEAN 		NOT NULL DEFAULT FALSE
)WITH(OIDS=FALSE);
COMMENT ON TABLE mxgraph_group_rel 							IS '图形设计-图库组关系';
COMMENT ON COLUMN mxgraph_group_rel.id                		IS '主键ID';
COMMENT ON COLUMN mxgraph_group_rel.mxgraph_group_id        IS '图形设计-图库组表主键ID';
COMMENT ON COLUMN mxgraph_group_rel.mxgraph_id              IS '图形设计表主键ID';
COMMENT ON COLUMN mxgraph_group_rel.created_at              IS '创建时间';
COMMENT ON COLUMN mxgraph_group_rel.creator_user_id         IS '创建人（用户ID）';
COMMENT ON COLUMN mxgraph_group_rel.creator_nickname        IS '创建人（昵称）';
COMMENT ON COLUMN mxgraph_group_rel.updated_at              IS '更新时间';
COMMENT ON COLUMN mxgraph_group_rel.updator_user_id         IS '更新人（用户ID）';
COMMENT ON COLUMN mxgraph_group_rel.updator_nickname        IS '更新人（昵称）';
COMMENT ON COLUMN mxgraph_group_rel.deleted                	IS '是否删除（默认否）';