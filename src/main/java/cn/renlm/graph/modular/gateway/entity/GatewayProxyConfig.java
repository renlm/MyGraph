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
 * 网关代理配置
 * </p>
 *
 * @author Renlm
 * @since 2022-07-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("gateway_proxy_config")
public class GatewayProxyConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理路径（/path/）
     */
    @TableField("path")
    private String path;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 代理服务器地址（多个逗号拼接）
     */
    @TableField("outgoing_servers")
    private String outgoingServers;

    /**
     * Access Key
     */
    @TableField("access_key")
    private String accessKey;

    /**
     * Secret Key
     */
    @TableField("secret_key")
    private String secretKey;

    /**
     * 是否启用（默认启用）
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 连接超时时间（秒）
     */
    @TableField("connection_timeout")
    private Integer connectionTimeout;

    /**
     * 读超时时间（秒）
     */
    @TableField("read_timeout")
    private Integer readTimeout;

    /**
     * 写超时时间（秒）
     */
    @TableField("write_timeout")
    private Integer writeTimeout;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 创建人（用户ID）
     */
    @TableField("creator_user_id")
    private String creatorUserId;

    /**
     * 创建人（昵称）
     */
    @TableField("creator_nickname")
    private String creatorNickname;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private Date updatedAt;

    /**
     * 更新人（用户ID）
     */
    @TableField("updator_user_id")
    private String updatorUserId;

    /**
     * 更新人（昵称）
     */
    @TableField("updator_nickname")
    private String updatorNickname;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
