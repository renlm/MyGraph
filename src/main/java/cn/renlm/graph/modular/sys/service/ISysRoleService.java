package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.entity.SysRole;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
public interface ISysRoleService extends IService<SysRole> {

	/**
	 * 获取用户角色列表
	 * 
	 * @param userId
	 * @return
	 */
	List<SysRole> findListByUser(String userId);

}
