package cn.renlm.mygraph.modular.graph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 图形设计
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-07-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("graph")
public class Graph implements Serializable {

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
     * 版本
     */
    @TableField("version")
    private Integer version;

    /**
     * 图形分类（编码）
     */
    @TableField("category_code")
    private String categoryCode;

    /**
     * 图形分类（名称）
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 封面图片
     */
    @TableField("cover")
    private String cover;

    /**
     * 缩放比例（默认1）
     */
    @TableField("zoom")
    private BigDecimal zoom;

    /**
     * 水平偏移量（默认0）
     */
    @TableField("dx")
    private Integer dx;

    /**
     * 垂直偏移量（默认0）
     */
    @TableField("dy")
    private Integer dy;

    /**
     * 显示网格（默认是）
     */
    @TableField("grid_enabled")
    private Boolean gridEnabled;

    /**
     * 网格大小（默认1）
     */
    @TableField("grid_size")
    private Integer gridSize;

    /**
     * 网格颜色
     */
    @TableField("grid_color")
    private String gridColor;

    /**
     * 页面视图（默认否）
     */
    @TableField("page_visible")
    private Boolean pageVisible;

    /**
     * 背景色
     */
    @TableField("background")
    private String background;

    /**
     * 显示连接箭头（默认否）
     */
    @TableField("connection_arrows_enabled")
    private Boolean connectionArrowsEnabled;

    /**
     * 显示连接点（默认是）
     */
    @TableField("connectable")
    private Boolean connectable;

    /**
     * 显示参考线（默认是）
     */
    @TableField("guides_enabled")
    private Boolean guidesEnabled;

    /**
     * XML文本
     */
    @TableField("xml")
    private String xml;

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
