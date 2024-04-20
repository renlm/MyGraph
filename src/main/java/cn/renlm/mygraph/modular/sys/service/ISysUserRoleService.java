package cn.renlm.mygraph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.modular.sys.entity.SysUserRole;

/**
 * <p>
 * 用户角色关系 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

	/**
	 * 保存用户角色关系
	 * 
	 * @param userId
	 * @param roleIds
	 */
	void saveRelationships(String userId, List<String> roleIds);

}
