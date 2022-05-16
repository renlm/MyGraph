package cn.renlm.graph.modular.ds.entity;

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
 * 数据源
 * </p>
 *
 * @author Renlm
 * @since 2022-05-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ds")
public class Ds implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * UUID
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * JDBC链接
     */
    @TableField("url")
    private String url;

    /**
     * 模式
     */
    @TableField("schema_name")
    private String schemaName;

    /**
     * 账号
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

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
     * 是否删除（默认否）
     */
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
