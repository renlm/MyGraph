package cn.renlm.graph.modular.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.dto.UserDto;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.security.User;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
public interface ISysUserService extends IService<SysUser> {

	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	UserDto findByUserId(String userId);

	/**
	 * 获取登录用户
	 * 
	 * @param username
	 * @return
	 */
	User loadUserByUsername(String username);

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<SysUser> findPage(Page<SysUser> page, UserDto form);

}
