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
 * 文档分类-收藏
 * </p>
 *
 * @author Renlm
 * @since 2022-07-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("doc_category_collect")
public class DocCategoryCollect implements Serializable {

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
     * 收藏人（用户ID）
     */
    @TableField("member_user_id")
    private String memberUserId;

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
     * 是否删除（默认否）
     */
    @TableField("deleted")
    private Boolean deleted;


}
