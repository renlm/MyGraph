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
 * 文档分类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("doc_category")
public class DocCategory implements Serializable {

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
     * 名称
     */
    @TableField("text")
    private String text;

    /**
     * 全路径名称
     */
    @TableField("fullname")
    private String fullname;

    /**
     * 图标
     */
    @TableField("icon_cls")
    private String iconCls;

    /**
     * 层级
     */
    @TableField("level")
    private Integer level;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 展开状态，open：无子节点、closed：有子节点
     */
    @TableField("state")
    private String state;

    /**
     * 父级ID
     */
    @TableField("pid")
    private Long pid;

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
