package cn.renlm.graph.service;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.DsDto;
import cn.renlm.graph.dto.UserDto;
import cn.renlm.graph.entity.Ds;

/**
 * <p>
 * 数据源 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-03-04
 */
public interface IDsService extends IService<Ds> {

	/**
	 * 初始化数据源
	 * 
	 * @param user
	 * @param ds
	 * @return
	 */
	Result init(UserDto user, DsDto ds);

	/**
	 * 根据Uuid删除数据源
	 * 
	 * @param user
	 * @param uuid
	 */
	void delByUuid(UserDto user, String uuid);

}
