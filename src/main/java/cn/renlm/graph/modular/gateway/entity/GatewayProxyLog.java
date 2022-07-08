package cn.renlm.graph.modular.gateway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 网关代理日志
 * </p>
 *
 * @author Renlm
 * @since 2022-07-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("gateway_proxy_log")
public class GatewayProxyLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 网关代理配置ID
     */
    @TableField("proxy_config_id")
    private Long proxyConfigId;

    /**
     * 代理配置-路径
     */
    @TableField("path")
    private String path;

    /**
     * 代理配置-名称
     */
    @TableField("name")
    private String name;

    /**
     * 代理配置-代理服务器地址
     */
    @TableField("outgoing_servers")
    private String outgoingServers;

    /**
     * 代理配置-连接超时时间（秒）
     */
    @TableField("connection_timeout")
    private Integer connectionTimeout;

    /**
     * 代理配置-读超时时间（秒）
     */
    @TableField("read_timeout")
    private Integer readTimeout;

    /**
     * 代理配置-写超时时间（秒）
     */
    @TableField("write_timeout")
    private Integer writeTimeout;

    /**
     * 代理配置-单服务限速（次/秒，默认1万）
     */
    @TableField("limit_for_second")
    private Integer limitForSecond;

    /**
     * 请求地址
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * 代理地址
     */
    @TableField("proxy_url")
    private String proxyUrl;

    /**
     * 请求方法
     */
    @TableField("http_method")
    private String httpMethod;

    /**
     * 请求时间
     */
    @TableField("request_time")
    private Date requestTime;

    /**
     * 响应时间
     */
    @TableField("response_time")
    private Date responseTime;

    /**
     * 响应码
     */
    @TableField("status_code")
    private Integer statusCode;

    /**
     * 响应消息
     */
    @TableField("status_text")
    private String statusText;

    /**
     * 错误消息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 耗时（毫秒）
     */
    @TableField("take_time")
    private Long takeTime;

    /**
     * 服务端ip
     */
    @TableField("server_ip")
    private String serverIp;

    /**
     * 客户端ip（地址）
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * 客户端ip（归属地）
     */
    @TableField("ip_region")
    private String ipRegion;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 用户表ID
     */
    @TableField("sys_user_id")
    private Long sysUserId;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;


}
