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
 * 系统登录日志
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-07-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_login_log")
public class SysLoginLog implements Serializable {

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
     * 账号
     */
    @TableField("username")
    private String username;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 客户端ip（地址）
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * 客户端ip（归属地）
     */
    @TableField("ip_region")
    private String ipRegion;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private Date loginTime;


}
