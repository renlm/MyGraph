package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.modular.sys.dto.ResourceDto;
import cn.renlm.graph.modular.sys.dto.UserDto;
import cn.renlm.graph.modular.sys.entity.SysUser;

/**
 * 角色授权
 * 
 * @author Renlm
 *
 */
public interface SysAuthAccessService {

	/**
	 * 获取全部资源列表并指定角色授权状态
	 * 
	 * @param roleId
	 * @return
	 */
	List<ResourceDto> getAllResourceByRoleId(String roleId);

	/**
	 * 获取角色授权资源列表
	 * 
	 * @param roleId
	 * @return
	 */
	List<ResourceDto> getAuthAccessResourceByRoleId(String roleId);

	/**
	 * 角色添加授权
	 * 
	 * @param roleId
	 * @param resourceIds
	 * @return
	 */
	List<ResourceDto> grant(String roleId, List<String> resourceIds);

	/**
	 * 角色取消授权
	 * 
	 * @param roleId
	 * @param resourceIds
	 * @return
	 */
	List<ResourceDto> ungrant(String roleId, List<String> resourceIds);

	/**
	 * 分页获取全部用户列表并指定角色授权状态
	 * 
	 * @param page
	 * @param roleId
	 * @param form
	 * @return
	 */
	Page<UserDto> getAllUserByRoleIdAndPage(Page<SysUser> page, String roleId, UserDto form);

	/**
	 * 添加角色授权人员
	 * 
	 * @param roleId
	 * @param userIds
	 */
	void addRoleMember(String roleId, List<String> userIds);

	/**
	 * 移除角色授权人员
	 * 
	 * @param roleId
	 * @param userIds
	 */
	void removeRoleMember(String roleId, List<String> userIds);

}