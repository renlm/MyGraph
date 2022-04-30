package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.entity.SysResource;

/**
 * <p>
 * 资源 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
public interface ISysResourceService extends IService<SysResource> {

	/**
	 * 获取用户资源列表
	 * 
	 * @param userId
	 * @return
	 */
	List<SysResource> findListByUser(String userId);

}
