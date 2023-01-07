package cn.renlm.graph.modular.oauth2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
@TableName("oauth2_authorization_consent")
public class Oauth2AuthorizationConsent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "registered_client_id", type = IdType.AUTO)
    private String registeredClientId;

    @TableId(value = "principal_name", type = IdType.AUTO)
    private String principalName;

    @TableField("authorities")
    private String authorities;


}
