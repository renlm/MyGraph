package cn.renlm.graph.modular.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 简易爬虫 - 访问请求
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2023-02-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("crawler_request")
public class CrawlerRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 站点代码
     */
    private String siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 入口链接
     */
    private String startUrl;

    /**
     * 匹配正则
     */
    private String regex;

    /**
     * 正则Group
     */
    private Integer regexGroup;

    /**
     * 页面链接类型，-1：入口链接，0：种子，1：数据
     */
    private Integer pageUrlType;

    /**
     * 爬取深度
     */
    private Integer depth;

    /**
     * 标记值
     */
    private String flag;

    /**
     * 页面链接
     */
    private String url;

    /**
     * 页面链接MD5值
     */
    private String urlMd5;

    /**
     * 访问来源
     */
    private String referer;

    /**
     * 请求响应码
     */
    private Integer statusCode;

    /**
     * 网页Title
     */
    private String htmlTitle;

    /**
     * 网页内容
     */
    private String htmlContent;

    /**
     * 网页截屏图片（Base64）
     */
    private String screenshotBase64;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 是否删除（默认否）
     */
    private Boolean deleted;


}
