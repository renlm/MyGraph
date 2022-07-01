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
 * Markdown文档-历史记录
 * </p>
 *
 * @author Renlm
 * @since 2022-07-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("markdown_history")
public class MarkdownHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 历史记录ID
     */
    @TableId(value = "history_id", type = IdType.AUTO)
    private Long historyId;

    /**
     * 变更说明
     */
    @TableField("change_label")
    private String changeLabel;

    /**
     * 操作时间
     */
    @TableField("operate_at")
    private Date operateAt;

    /**
     * 操作人（用户ID）
     */
    @TableField("operator_user_id")
    private String operatorUserId;

    /**
     * 操作人（昵称）
     */
    @TableField("operator_nickname")
    private String operatorNickname;

    /**
     * Markdown文档表主键
     */
    @TableField("markdown_id")
    private Long markdownId;

    /**
     * Markdown文档UUID
     */
    @TableField("markdown_uuid")
    private String markdownUuid;

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
