package cn.renlm.graph.dto;

import java.io.Serializable;
import java.util.Date;

import cn.hutool.core.bean.BeanUtil;
import cn.renlm.graph.modular.sys.entity.SysUser;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户基本信息
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
public class UserBase implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户表ID
	 */
	private Long id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 登录账号
	 */
	private String username;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 真实姓名
	 */
	private String realname;

	/**
	 * 出生日期
	 */
	private Date birthday;

	/**
	 * 性别，M：男，F：女
	 */
	private String sex;

	/**
	 * 手机号码
	 */
	private String mobile;

	/**
	 * 邮箱地址
	 */
	private String email;

	/**
	 * 个性签名
	 */
	private String sign;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 封装
	 * 
	 * @param user
	 * @return
	 */
	public static final UserBase of(SysUser user) {
		UserBase userBase = BeanUtil.copyProperties(user, UserBase.class);
		return userBase;
	}

}
