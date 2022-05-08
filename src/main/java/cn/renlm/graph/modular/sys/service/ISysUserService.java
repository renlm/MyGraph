package cn.renlm.graph.modular.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.dto.SysUserDto;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.response.Result;

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
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<SysUser> findPage(Page<SysUser> page, SysUserDto form);

	/**
	 * 获取登录用户
	 * 
	 * @param username
	 * @return
	 */
	User loadUserByUsername(String username);

	/**
	 * 修改个人信息
	 * 
	 * @param id
	 * @param form
	 */
	void doModifyPersonal(Long id, SysUser form);

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param form
	 * @return
	 */
	Result<SysUserDto> ajaxSave(SysUserDto form);

}
