package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.dto.OrgChartDto;
import cn.renlm.graph.modular.sys.dto.OrgDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;

/**
 * <p>
 * 组织机构 服务类
 * </p>
 *
 * @author Renlm
 * @since 2021-04-19
 */
public interface ISysOrgService extends IService<SysOrg> {

	/**
	 * 获取用户组织机构列表
	 * 
	 * @param userId
	 * @return
	 */
	List<OrgChartDto> findListByUserId(String userId);

	/**
	 * 获取组织机构树
	 * 
	 * @return
	 */
	List<OrgChartDto> findTreeList();

	/**
	 * 获取指定层级组织机构列表
	 * 
	 * @param level
	 * @return
	 */
	List<OrgDto> findListByLevel(Integer level);

	/**
	 * 获取指定父节点下级组织机构列表
	 * 
	 * @param pid
	 * @return
	 */
	List<OrgDto> findListByPid(Long pid);

	/**
	 * 获取由下而上的子父集
	 * 
	 * @param id
	 * @return
	 */
	List<SysOrg> findFathers(Long id);

	/**
	 * 获取指定父节点及其下所有子节点
	 * 
	 * @param pid
	 * @return
	 */
	List<OrgDto> findChilds(Long pid);

	/**
	 * 根据组织机构ID获取详细信息
	 * 
	 * @param orgId
	 * @return
	 */
	OrgDto findDetailByOrgId(String orgId);

	/**
	 * 根据组织机构ID级联删除
	 * 
	 * @param orgId
	 * @return
	 */
	int delCascadeByOrgId(String orgId);

}
