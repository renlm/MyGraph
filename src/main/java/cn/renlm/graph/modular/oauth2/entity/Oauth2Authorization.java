package cn.renlm.graph.modular.oauth2.entity;

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
 * 
 * </p>
 *
 * @author Renlm
 * @since 2023-01-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("oauth2_authorization")
public class Oauth2Authorization implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @TableField("registered_client_id")
    private String registeredClientId;

    @TableField("principal_name")
    private String principalName;

    @TableField("authorization_grant_type")
    private String authorizationGrantType;

    @TableField("authorized_scopes")
    private String authorizedScopes;

    @TableField("attributes")
    private byte[] attributes;

    @TableField("state")
    private String state;

    @TableField("authorization_code_value")
    private byte[] authorizationCodeValue;

    @TableField("authorization_code_issued_at")
    private Date authorizationCodeIssuedAt;

    @TableField("authorization_code_expires_at")
    private Date authorizationCodeExpiresAt;

    @TableField("authorization_code_metadata")
    private byte[] authorizationCodeMetadata;

    @TableField("access_token_value")
    private byte[] accessTokenValue;

    @TableField("access_token_issued_at")
    private Date accessTokenIssuedAt;

    @TableField("access_token_expires_at")
    private Date accessTokenExpiresAt;

    @TableField("access_token_metadata")
    private byte[] accessTokenMetadata;

    @TableField("access_token_type")
    private String accessTokenType;

    @TableField("access_token_scopes")
    private String accessTokenScopes;

    @TableField("oidc_id_token_value")
    private byte[] oidcIdTokenValue;

    @TableField("oidc_id_token_issued_at")
    private Date oidcIdTokenIssuedAt;

    @TableField("oidc_id_token_expires_at")
    private Date oidcIdTokenExpiresAt;

    @TableField("oidc_id_token_metadata")
    private byte[] oidcIdTokenMetadata;

    @TableField("refresh_token_value")
    private byte[] refreshTokenValue;

    @TableField("refresh_token_issued_at")
    private Date refreshTokenIssuedAt;

    @TableField("refresh_token_expires_at")
    private Date refreshTokenExpiresAt;

    @TableField("refresh_token_metadata")
    private byte[] refreshTokenMetadata;


}
