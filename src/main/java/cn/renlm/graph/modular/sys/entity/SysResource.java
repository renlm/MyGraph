package cn.renlm.graph.modular.sys.entity;

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
 * 资源
 * </p>
 *
 * @author Renlm
 * @since 2022-05-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_resource")
public class SysResource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资源ID
     */
    @TableField("resource_id")
    private String resourceId;

    /**
     * 代码
     */
    @TableField("code")
    private String code;

    /**
     * 名称
     */
    @TableField("text")
    private String text;

    /**
     * 资源类型（编码）
     */
    @TableField("resource_type_code")
    private String resourceTypeCode;

    /**
     * 资源地址或标识
     */
    @TableField("url")
    private String url;

    /**
     * 图标
     */
    @TableField("icon_cls")
    private String iconCls;

    /**
     * 图标颜色
     */
    @TableField("icon_cls_colour")
    private String iconClsColour;

    /**
     * 资源名称颜色
     */
    @TableField("text_colour")
    private String textColour;

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
     * 展开状态，open：无子菜单、closed：有子菜单
     */
    @TableField("state")
    private String state;

    /**
     * 是否为默认主页（默认否）
     */
    @TableField("default_home_page")
    private Boolean defaultHomePage;

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
     * 更新时间
     */
    @TableField("updated_at")
    private Date updatedAt;

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
