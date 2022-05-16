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
 * 用户
 * </p>
 *
 * @author Renlm
 * @since 2022-05-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 登录账号
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 真实姓名
     */
    @TableField("realname")
    private String realname;

    /**
     * 出生日期
     */
    @TableField("birthday")
    private Date birthday;

    /**
     * 性别，M：男，F：女
     */
    @TableField("sex")
    private String sex;

    /**
     * 手机号码
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 邮箱地址
     */
    @TableField("email")
    private String email;

    /**
     * 个性签名
     */
    @TableField("sign")
    private String sign;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 是否启用（默认启用）
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 账户未过期（默认未过期）
     */
    @TableField("account_non_expired")
    private Boolean accountNonExpired;

    /**
     * 凭证未过期（默认未过期）
     */
    @TableField("credentials_non_expired")
    private Boolean credentialsNonExpired;

    /**
     * 账号未锁定（默认未锁定）
     */
    @TableField("account_non_locked")
    private Boolean accountNonLocked;

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
