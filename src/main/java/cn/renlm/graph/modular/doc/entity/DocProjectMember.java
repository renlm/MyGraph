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
 * 文档项目-成员
 * </p>
 *
 * @author Renlm
 * @since 2022-06-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("doc_project_member")
public class DocProjectMember implements Serializable {

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
     * 成员（用户ID）
     */
    @TableField("member_user_id")
    private String memberUserId;

    /**
     * 角色，1：浏览者，2：编辑者，3：管理员
     */
    @TableField("role")
    private Integer role;

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
