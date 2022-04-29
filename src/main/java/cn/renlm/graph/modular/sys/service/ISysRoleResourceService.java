package cn.renlm.graph.modular.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.dto.RoleResourceDto;
import cn.renlm.graph.modular.sys.entity.SysRoleResource;

/**
 * <p>
 * 角色资源关系 服务类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
public interface ISysRoleResourceService extends IService<SysRoleResource> {

	/**
	 * 根据主键ID获取详细信息
	 * 
	 * @param id
	 * @return
	 */
	RoleResourceDto findDetailById(Long id);

}
