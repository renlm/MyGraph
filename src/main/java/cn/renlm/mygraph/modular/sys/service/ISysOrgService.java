package cn.renlm.mygraph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.tree.Tree;
import cn.renlm.mygraph.modular.sys.dto.SysOrgDto;
import cn.renlm.mygraph.modular.sys.entity.SysOrg;
import cn.renlm.plugins.MyResponse.Result;

/**
 * <p>
 * 组织机构 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface ISysOrgService extends IService<SysOrg> {

	/**
	 * 获取用户组织机构列表
	 * 
	 * @param userId
	 * @return
	 */
	List<SysOrgDto> findListByUser(String userId);

	/**
	 * 获取指定父节点下级列表
	 * 
	 * @param pid
	 * @return
	 */
	List<SysOrg> findListByPid(Long pid);

	/**
	 * 获取由上而下的父子集
	 * 
	 * @param id
	 * @return
	 */
	List<SysOrg> findFathers(Long id);

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
	 * @param sysOrg
	 * @return
	 */
	Result<SysOrg> ajaxSave(SysOrg sysOrg);

}
