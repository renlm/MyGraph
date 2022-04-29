package cn.renlm.graph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.dto.UserOrgDto;
import cn.renlm.graph.modular.sys.entity.SysUserOrg;

/**
 * <p>
 * 用户组织机构关系 服务类
 * </p>
 *
 * @author Renlm
 * @since 2021-04-20
 */
public interface ISysUserOrgService extends IService<SysUserOrg> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<UserOrgDto> findPage(Page<SysUserOrg> page, UserOrgDto form);

	/**
	 * 保存用户组织机构关系
	 * 
	 * @param userId
	 * @param orgIds
	 */
	void saveRelationships(String userId, List<String> orgIds);

}
