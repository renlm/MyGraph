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
 * @since 2022-04-29
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
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 性别，M：男，F：女
     */
    @TableField("sex")
    private String sex;

    /**
     * 出生日期
     */
    @TableField("birthday")
    private Date birthday;

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
     * 所属区域
     */
    @TableField("zone")
    private String zone;

    /**
     * 所属区域（国家编码）
     */
    @TableField("country_code")
    private String countryCode;

    /**
     * 所属区域（国家名称）
     */
    @TableField("country_name")
    private String countryName;

    /**
     * 所属区域（省编码）
     */
    @TableField("province_code")
    private String provinceCode;

    /**
     * 所属区域（省名称）
     */
    @TableField("province_name")
    private String provinceName;

    /**
     * 所属区域（市编码）
     */
    @TableField("city_code")
    private String cityCode;

    /**
     * 所属区域（市名称）
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 所属区域（区县编码）
     */
    @TableField("district_code")
    private String districtCode;

    /**
     * 所属区域（区县名称）
     */
    @TableField("district_name")
    private String districtName;

    /**
     * 籍贯（编码）
     */
    @TableField("native_place_code")
    private String nativePlaceCode;

    /**
     * 民族（编码）
     */
    @TableField("nation_code")
    private String nationCode;

    /**
     * 毕业院校
     */
    @TableField("graduate_school")
    private String graduateSchool;

    /**
     * 学历（编码）
     */
    @TableField("education_code")
    private String educationCode;

    /**
     * 学位（编码）
     */
    @TableField("degree_code")
    private String degreeCode;

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
