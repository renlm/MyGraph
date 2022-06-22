package cn.renlm.graph.modular.doc.entity;

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
 * 文档项目
 * </p>
 *
 * @author Renlm
 * @since 2022-06-22
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("doc_project")
public class DocProject implements Serializable {

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
     * 项目名称
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 访问级别，1：私有（默认，只有项目成员才可以访问），2：公开（可以由任何登录用户访问）
     */
    @TableField("visit_level")
    private Integer visitLevel;

    /**
     * 是否允许分享（默认是，该项目下的文档是否允许分享）
     */
    @TableField("is_share")
    private Boolean isShare;

    /**
     * 标签（多个逗号分隔）
     */
    @TableField("tags")
    private String tags;

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
