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
 * 用户组织机构关系
 * </p>
 *
 * @author Renlm
 * @since 2022-07-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_user_org")
public class SysUserOrg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户表（主键ID）
     */
    @TableField("sys_user_id")
    private Long sysUserId;

    /**
     * 组织机构表（主键ID）
     */
    @TableField("sys_org_id")
    private Long sysOrgId;

    /**
     * 职位编码
     */
    @TableField("position_code")
    private String positionCode;

    /**
     * 职位名称
     */
    @TableField("position_name")
    private String positionName;

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
