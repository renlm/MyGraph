package cn.renlm.graph.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.renlm.graph.modular.sys.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户基本信息
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserBase extends SysUser {

	private static final long serialVersionUID = 1L;

	/**
	 * 会话ID
	 */
	private String SESSION;

	/**
	 * 封装
	 * 
	 * @param user
	 * @param SESSION
	 * @return
	 */
	public static final UserBase of(SysUser user, String SESSION) {
		UserBase userBase = BeanUtil.copyProperties(user, UserBase.class);
		userBase.setSESSION(SESSION);
		userBase.setPassword(null);
		return userBase;
	}
}