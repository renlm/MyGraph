package com.lei.du.huawei.entity;

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
 * 虚拟号-鉴权配置
 * </p>
 *
 * @author Renlm
 * @since 2022-02-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("virtual_phone_key")
public class VirtualPhoneKey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 系统标识
     */
    @TableField("ak")
    private String ak;

    /**
     * 私钥（Base64）
     */
    @TableField("private_key")
    private String privateKey;

    /**
     * 公钥（Base64）
     */
    @TableField("public_key")
    private String publicKey;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private Date updatedAt;

    /**
     * 是否禁用（默认否）
     */
    @TableField("disabled")
    private Boolean disabled;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
