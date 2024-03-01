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
 * 系统常量
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-07-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_const")
public class SysConst implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 常量ID
     */
    @TableId(value = "const_id", type = IdType.ASSIGN_ID)
    private String constId;

    /**
     * 代码
     */
    @TableField("code")
    private String code;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 值
     */
    @TableField("val")
    private String val;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

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
     * 备注
     */
    @TableField("remark")
    private String remark;


}
