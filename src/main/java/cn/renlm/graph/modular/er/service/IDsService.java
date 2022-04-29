package cn.renlm.graph.modular.er.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.common.Result;
import cn.renlm.graph.modular.er.dto.DsDto;
import cn.renlm.graph.modular.er.entity.Ds;
import cn.renlm.graph.security.User;

/**
 * <p>
 * 数据源 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
public interface IDsService extends IService<Ds> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<Ds> findPage(Page<Ds> page, User user, DsDto form);

	/**
	 * 初始化数据源
	 * 
	 * @param user
	 * @param ds
	 * @return
	 */
	Result<String> init(User user, DsDto ds);

	/**
	 * 根据Uuid删除数据源
	 * 
	 * @param user
	 * @param uuid
	 */
	void delByUuid(User user, String uuid);

}
