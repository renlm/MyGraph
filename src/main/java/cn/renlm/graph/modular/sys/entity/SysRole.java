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
 * 角色
 * </p>
 *
 * @author Renlm
 * @since 2022-06-18
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

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
     * 展开状态，open：无子角色、closed：有子角色
     */
    @TableField("state")
    private String state;

    /**
     * 父级id
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
