package cn.renlm.graph.modular.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.dto.User;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
public interface ISysUserService extends IService<SysUser> {
	
	/**
	 * 获取登录用户
	 * 
	 * @param username
	 * @return
	 */
	User loadUserByUsername(String username);

}
