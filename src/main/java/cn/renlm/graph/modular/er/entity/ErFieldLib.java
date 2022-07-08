package cn.renlm.graph.modular.er.entity;

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
 * ER模型-我的字段库
 * </p>
 *
 * @author Renlm
 * @since 2022-07-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("er_field_lib")
public class ErFieldLib implements Serializable {

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
     * 列名
     */
    @TableField("name")
    private String name;

    /**
     * 注释
     */
    @TableField("comment")
    private String comment;

    /**
     * java.sql.Types
     */
    @TableField("sql_type")
    private Integer sqlType;

    /**
     * JdbcType
     */
    @TableField("jdbc_type")
    private String jdbcType;

    /**
     * 长度
     */
    @TableField("size")
    private Long size;

    /**
     * 精度
     */
    @TableField("digit")
    private Integer digit;

    /**
     * 是否可为空
     */
    @TableField("is_nullable")
    private Boolean isNullable;

    /**
     * 是否自增
     */
    @TableField("auto_increment")
    private Boolean autoIncrement;

    /**
     * 字段默认值
     */
    @TableField("column_def")
    private String columnDef;

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
