package cn.renlm.graph.modular.markdown.entity;

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
 * 文档项目-标签
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("doc_project_tag")
public class DocProjectTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文档项目ID
     */
    @TableField("doc_project_id")
    private Long docProjectId;

    /**
     * UUID
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

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
