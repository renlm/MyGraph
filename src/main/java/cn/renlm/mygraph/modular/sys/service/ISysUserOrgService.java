package cn.renlm.mygraph.modular.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.modular.sys.entity.SysUserOrg;

/**
 * <p>
 * 用户组织机构关系 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface ISysUserOrgService extends IService<SysUserOrg> {

	/**
	 * 保存用户组织机构关系
	 * 
	 * @param userId
	 * @param orgIds
	 */
	void saveRelationships(String userId, List<String> orgIds);

}
