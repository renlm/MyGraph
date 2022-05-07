package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.response.Result;

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

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param pid
	 * @return
	 */
	List<SysRole> findListByPid(Long pid);

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param id
	 * @return
	 */
	List<SysRole> findFathers(Long id);

	/**
	 * 获取树形结构
	 * 
	 * @param root
	 * @param pid
	 * @return
	 */
	List<Tree<Long>> getTree(boolean root, Long pid);

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param request
	 * @param sysRole
	 * @return
	 */
	Result<SysRole> ajaxSave(SysRole sysRole);

}
