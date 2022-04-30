package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.entity.SysRoleResource;

/**
 * <p>
 * 角色资源关系 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
public interface ISysRoleResourceService extends IService<SysRoleResource> {

	/**
	 * 获取用户的角色资源关系列表
	 * 
	 * @param userId
	 * @return
	 */
	List<SysRoleResource> findListByUser(String userId);

}
