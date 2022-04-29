package cn.renlm.graph.modular.sys.service;

import java.util.List;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.common.Resource;
import cn.renlm.graph.modular.sys.dto.ResourceDto;
import cn.renlm.graph.modular.sys.entity.SysResource;

/**
 * <p>
 * 资源 服务类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
public interface ISysResourceService extends IService<SysResource> {

	/**
	 * 根据指定用户ID获取默认主页
	 * 
	 * @param userId
	 * @return
	 */
	List<SysResource> findHomePagesByUserId(String userId);

	/**
	 * 根据指定用户ID获取父节点下级资源
	 * 
	 * @param userId
	 * @param pid
	 * @param types
	 * @return
	 */
	List<ResourceDto> findChildsByUserId(String userId, Long pid, Resource... types);

	/**
	 * 根据指定用户ID获取常用菜单
	 * 
	 * @param userId
	 * @param text
	 * @return
	 */
	List<ResourceDto> findCommonMenusByUserId(String userId, String text);

	/**
	 * 获取资源资源列表
	 * 
	 * @param sysResourceIds
	 * @param wrapper
	 * @return
	 */
	List<ResourceDto> findListBySysRoleIds(List<Long> sysResourceIds,
			Consumer<LambdaQueryWrapper<SysResource>> wrapper);

	/**
	 * 获取指定层级资源列表
	 * 
	 * @param level
	 * @return
	 */
	List<ResourceDto> findListByLevel(Integer level);

	/**
	 * 获取指定父节点下级资源列表
	 * 
	 * @param pid
	 * @return
	 */
	List<ResourceDto> findListByPid(Long pid);

	/**
	 * 获取由下而上的子父集
	 * 
	 * @param id
	 * @return
	 */
	List<SysResource> findFathers(Long id);

	/**
	 * 获取指定父节点及其下所有子节点
	 * 
	 * @param pid
	 * @return
	 */
	List<ResourceDto> findChilds(Long pid);

	/**
	 * 根据资源ID获取详细信息
	 * 
	 * @param resourceId
	 * @return
	 */
	ResourceDto findDetailByResourceId(String resourceId);

	/**
	 * 根据资源ID级联删除
	 * 
	 * @param resourceId
	 * @return
	 */
	int delCascadeByResourceId(String resourceId);

}
