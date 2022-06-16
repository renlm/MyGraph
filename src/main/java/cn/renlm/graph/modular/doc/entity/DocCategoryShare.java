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
 * 文档分类-分享
 * </p>
 *
 * @author Renlm
 * @since 2022-06-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("doc_category_share")
public class DocCategoryShare implements Serializable {

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
     * 文档分类ID
     */
    @TableField("doc_category_id")
    private Long docCategoryId;

    /**
     * UUID
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 分享类型，1：公开，2：密码查看
     */
    @TableField("share_type")
    private Integer shareType;

    /**
     * 访问密码
     */
    @TableField("password")
    private String password;

    /**
     * 有效期类型（-1，永久，7:七天，30：三十天）
     */
    @TableField("effective_type")
    private Integer effectiveType;

    /**
     * 有效截止日期
     */
    @TableField("deadline")
    private Date deadline;

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
     * 是否禁用（默认否）
     */
    @TableField("disabled")
    private Boolean disabled;

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
