package cn.renlm.mygraph.modular.markdown.entity;

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
 * Markdown文档
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-07-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("markdown")
public class Markdown implements Serializable {

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
     * 文档名称
     */
    @TableField("name")
    private String name;

    /**
     * 版本
     */
    @TableField("version")
    private Integer version;

    /**
     * 文档内容
     */
    @TableField("content")
    private String content;

    /**
     * 图形设计UUID
     */
    @TableField("graph_uuid")
    private String graphUuid;

    /**
     * 图形设计版本
     */
    @TableField("graph_version")
    private Integer graphVersion;

    /**
     * 数据表格
     */
    @TableField("data_table")
    private String dataTable;

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
