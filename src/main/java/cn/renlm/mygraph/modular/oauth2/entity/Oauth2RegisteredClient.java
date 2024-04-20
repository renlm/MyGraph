package cn.renlm.mygraph.modular.oauth2.entity;

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
 * Oauth2.0 注册客户端
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2023-01-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("oauth2_registered_client")
public class Oauth2RegisteredClient implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField("client_id")
    private String clientId;

    @TableField("client_id_issued_at")
    private Date clientIdIssuedAt;

    @TableField("client_secret")
    private String clientSecret;

    @TableField("client_secret_expires_at")
    private Date clientSecretExpiresAt;

    @TableField("client_name")
    private String clientName;

    @TableField("client_authentication_methods")
    private String clientAuthenticationMethods;

    @TableField("authorization_grant_types")
    private String authorizationGrantTypes;

    @TableField("redirect_uris")
    private String redirectUris;

    @TableField("scopes")
    private String scopes;

    @TableField("client_settings")
    private String clientSettings;

    @TableField("token_settings")
    private String tokenSettings;


}
