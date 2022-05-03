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
 * 数据字典
 * </p>
 *
 * @author Renlm
 * @since 2022-05-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_dict")
public class SysDict implements Serializable {

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
     * 简称
     */
    @TableField("abbreviation")
    private String abbreviation;

    /**
     * 别名
     */
    @TableField("alias")
    private String alias;

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
     * 备注
     */
    @TableField("remark")
    private String remark;


}
