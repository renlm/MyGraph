package cn.renlm.mygraph.modular.sys.entity;

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
 * 角色资源关系
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-07-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_role_resource")
public class SysRoleResource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色表（主键ID）
     */
    @TableField("sys_role_id")
    private Long sysRoleId;

    /**
     * 资源表（主键ID）
     */
    @TableField("sys_resource_id")
    private Long sysResourceId;

    /**
     * 别名
     */
    @TableField("alias")
    private String alias;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 是否为默认主页
     */
    @TableField("default_home_page")
    private Boolean defaultHomePage;

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
