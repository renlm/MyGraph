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
 * 虚拟号-绑定记录（AX模式）
 * </p>
 *
 * @author Renlm
 * @since 2022-02-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("virtual_phone_ax")
public class VirtualPhoneAx implements Serializable {

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
     * 真实号码
     */
    @TableField("orig_num")
    private String origNum;

    /**
     * 绑定虚拟号
     */
    @TableField("virtual_phone")
    private String virtualPhone;

    /**
     * 绑定状态，0：已解绑，1：绑定中
     */
    @TableField("status")
    private Integer status;

    /**
     * 绑定时间
     */
    @TableField("bind_time")
    private Date bindTime;

    /**
     * 解绑时间
     */
    @TableField("unbind_time")
    private Date unbindTime;

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
     * 备注
     */
    @TableField("remark")
    private String remark;


}
