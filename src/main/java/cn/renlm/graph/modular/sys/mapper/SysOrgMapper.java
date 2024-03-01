package cn.renlm.graph.modular.sys.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.renlm.graph.modular.sys.dto.SysOrgDto;
import cn.renlm.graph.modular.sys.entity.SysOrg;

/**
 * <p>
 * 组织机构 Mapper 接口
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface SysOrgMapper extends BaseMapper<SysOrg> {

	/**
	 * 获取用户组织机构列表
	 * 
	 * @param userId
	 * @return
	 */
	List<SysOrgDto> findListByUser(String userId);

}
