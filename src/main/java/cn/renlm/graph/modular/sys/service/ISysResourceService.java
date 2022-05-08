package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.modular.sys.dto.SysResourceDto;
import cn.renlm.graph.modular.sys.entity.SysResource;
import cn.renlm.graph.response.Result;

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

	/**
	 * 获取角色资源列表
	 * 
	 * @param roleId
	 * @return
	 */
	List<SysResourceDto> findListByRole(String roleId);

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param pid
	 * @return
	 */
	List<SysResource> findListByPid(Long pid);

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param id
	 * @return
	 */
	List<SysResource> findFathers(Long id);

	/**
	 * 获取树形结构
	 * 
	 * @param root
	 * @param pid
	 * @param includeDisabled
	 * @return
	 */
	List<Tree<Long>> getTree(boolean root, Long pid, Boolean includeDisabled);

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param request
	 * @param sysResource
	 * @return
	 */
	Result<SysResource> ajaxSave(SysResource sysResource);

}
