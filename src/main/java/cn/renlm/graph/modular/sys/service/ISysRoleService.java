package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.dto.RoleDto;
import cn.renlm.graph.modular.sys.entity.SysRole;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author Renlm
 * @since 2020-01-01
 */
public interface ISysRoleService extends IService<SysRole> {

	/**
	 * 获取用户角色列表
	 * 
	 * @param userId
	 * @return
	 */
	List<SysRole> findListByUserId(String userId);

	/**
	 * 获取角色树
	 * 
	 * @return
	 */
	List<SysRole> findTreeList();

	/**
	 * 获取指定层级角色列表
	 * 
	 * @param level
	 * @return
	 */
	List<RoleDto> findListByLevel(Integer level);

	/**
	 * 获取指定父节点下级角色列表
	 * 
	 * @param pid
	 * @return
	 */
	List<RoleDto> findListByPid(Long pid);

	/**
	 * 获取由下而上的子父集
	 * 
	 * @param id
	 * @return
	 */
	List<SysRole> findFathers(Long id);

	/**
	 * 获取指定父节点及其下所有子节点
	 * 
	 * @param pid
	 * @return
	 */
	List<RoleDto> findChilds(Long pid);

	/**
	 * 根据角色ID获取详细信息
	 * 
	 * @param roleId
	 * @return
	 */
	RoleDto findDetailByRoleId(String roleId);

}
